package ktor.chat.client

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.ktor.chat.*
import io.ktor.chat.Nothing
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ktor.chat.utils.Done
import ktor.chat.utils.Remote
import ktor.chat.utils.load

inline fun <reified E: Identifiable<ID>, ID> restRepository(
    crossinline http: () -> HttpClient,
    path: String,
) = object : Repository<E, ID> {

    override suspend fun get(id: ID): E? =
        http().get("$path/$id").body()

    override suspend fun create(e: E): E =
        http().post(path) {
            setBody(e)
        }.body()

    override suspend fun update(e: E) {
        http().put(path) {
            setBody(e)
        }
    }

    override suspend fun delete(id: ID) {
        http().delete("$path/$id")
    }

    override suspend fun list(query: Query): List<E> =
        if (query == Nothing)
            emptyList()
        else http().get("$path${query.toQueryString()}").body()

    private fun Query.toQueryString() =
        when(this) {
            is Everything, Nothing -> ""
            is MapQuery -> '?' + parametersOf(
                mapValues { (_, values) -> values.map { it.toString() }  }
            ).formUrlEncode()
        }


}

inline fun <reified E : Identifiable<ID>, ID> observableRepository(
    crossinline http: () -> HttpClient,
    path: String,
): ObservableRepository<E, ID> =
    object : Repository<E, ID> by restRepository(http, path), ObservableRepository<E, ID> {
        val observers = mutableMapOf<Observer<E>, Job>()

        override fun onChange(observer: Observer<E>): Observer<E> {
            observers[observer] = http().launch(Dispatchers.IO) {
                http().sse("$path/changes") {
                    incoming.cancellable().collect { event ->
                        event.data?.let { data ->
                            val (changeTypeName, entityJson) = data.split(" ", limit = 2)
                            val changeType = ChangeType.valueOf(changeTypeName)
                            val entity = Json.decodeFromString<E>(entityJson)
                            observer(changeType, entity)
                        }
                    }
                }
            }
            return observer
        }

        override fun forget(observer: Observer<E>) {
            observers.remove(observer)?.cancel()
        }

    }

@Composable
inline fun <reified E: Identifiable<ID>, ID> Repository<E, ID>.remoteList(query: Query = Everything): State<Remote<List<E>>> =
    load(query) { list(query) }

@Composable
inline fun <reified E: Identifiable<ID>, ID> ObservableRepository<E, ID>.remoteListWithUpdates(query: Query = Everything): State<Remote<SnapshotStateList<E>>> {
    val remoteList = load(query) { mutableStateListOf(*list(query).toTypedArray()) }
    val predicate = query.toPredicate(E::class)
    
    DisposableEffect(query) {
        val observer = onChange { changeType, e ->
            val currentList = (remoteList.value as? Done ?: return@onChange).value
            if (!predicate(e)) return@onChange
            
            when(changeType) {
                ChangeType.CREATE -> currentList.add(e)
                ChangeType.UPDATE -> currentList.replaceAll { if (it.id == e.id) e else it }
                ChangeType.DELETE -> currentList.removeIf { it.id == e.id }
            }
        }
        onDispose {
            forget(observer)
        }
    }
    
    return remoteList
}

@Composable
fun ObservableRepository<Message, Long>.listInRoom(room: Room?): State<Remote<SnapshotStateList<Message>>> =
    remoteListWithUpdates(
        room?.let {
            MapQuery {
                this["room"] = room.id
            }
        } ?: Nothing
    )