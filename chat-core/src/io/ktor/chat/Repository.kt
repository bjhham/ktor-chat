package io.ktor.chat

interface Repository<E: Identifiable<ID>, ID> : ReadOnlyRepository<E, ID> {
    suspend fun create(e: E): E
    suspend fun update(e: E)
    suspend fun delete(id: ID)
}

interface ReadOnlyRepository<out E: Identifiable<ID>, ID> {
    suspend fun get(id: ID): E?
    suspend fun list(query: Query = Everything): List<E>
}

sealed interface Query

class MapQuery(private val map: Map<String, List<String>>): Query, Map<String, List<String>> by map {
    class Builder {
        private val map = mutableMapOf<String, List<String>>()

        operator fun set(key: String, value: String) {
            map[key] = map[key].orEmpty() + value
        }

        fun build() = MapQuery(map)
    }
}
data object Everything: Query

/**
 * In-memory implementation for repository, used for testing.
 */
class ListRepository<E: Identifiable<ID>, ID>(
    private val list: MutableList<E> = mutableListOf(),
    private var id: ID,
    private val nextId: (ID) -> ID,
    private val setId: (E, ID) -> E,
): Repository<E, ID> {
    companion object {
        fun <E: Identifiable<Long>> create(
            vararg items: E,
            setId: (E, Long) -> E
        ) = ListRepository(
            list = items.mapIndexed { index, e -> setId(e, index.toLong() + 1L) }.toMutableList(),
            id = items.size.toLong(),
            nextId = { it + 1 },
            setId = setId
        )
    }

    override suspend fun get(id: ID): E? =
        list.find { it.id == id }

    override suspend fun create(e: E): E =
        setId(e, nextId(id)).also {
            list.add(it)
        }

    override suspend fun update(e: E) {
        val index = findIndex(e.id) ?: return 
        list[index] = e
    }

    override suspend fun delete(id: ID) {
        val index = findIndex(id)
        list.removeAt(index)
    }
    
    private fun findIndex(id: ID): Int =
        list.indexOfFirst {
            it.id == id
        }

    override suspend fun list(query: Query): List<E> =
        list
}

inline fun <reified E> Query.toPredicate(): (E) -> Boolean =
    when(this) {
        is Everything -> {{ true }}
        else -> {
            val type = E::class
            // type.members
            { true }
        }
    }


suspend fun <E: Identifiable<ID>, ID> Repository<E, ID>.list(params: (MapQuery.Builder) -> Unit): List<E> =
    list(MapQuery.Builder().also(params).build())