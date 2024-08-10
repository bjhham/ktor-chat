package ktor.chat.client

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.ktor.chat.*
import ktor.chat.utils.Done
import ktor.chat.utils.Remote
import ktor.chat.utils.load

@Composable
inline fun <reified E: Identifiable<ID>, ID> Repository<E, ID>.remoteList(): State<Remote<List<E>>> =
    load { list() }

@Composable
inline fun <reified E: Identifiable<ID>, ID> ObservableRepository<E, ID>.remoteListWithUpdates(): State<Remote<SnapshotStateList<E>>> {
    val remoteList = load { mutableStateListOf(*list().toTypedArray()) }
    
    DisposableEffect(true) {
        val observer = onChange { changeType, e ->
            val currentList = (remoteList.value as? Done ?: return@onChange).value
            
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