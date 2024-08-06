package io.ktor.chat

interface Repository<E: Identifiable<ID>, ID> {
    suspend fun get(id: ID): E?
    suspend fun create(e: E): E
    suspend fun update(e: E)
    suspend fun delete(id: ID)
    suspend fun list(): List<E>
}

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
            list = items.toMutableList(),
            id = 0L,
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

    override suspend fun list(): List<E> =
        list
}