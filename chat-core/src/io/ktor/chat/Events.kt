package io.ktor.chat

interface ObservableRepository<E: Identifiable<ID>, ID>: Repository<E, ID> {
    fun onChange(observer: Observer<E>): Observer<E>
    fun forget(observer: Observer<E>)
}

fun <E: Identifiable<ID>, ID> Repository<E, ID>.observable(): ObservableRepository<E, ID> =
    WatchListObservableRepository(this)

typealias Observer<E> = suspend (ChangeType, E) -> Unit

enum class ChangeType {
    CREATE,
    UPDATE,
    DELETE,
}

suspend fun <E> Observer<E>.create(e: E) = invoke(ChangeType.CREATE, e)
suspend fun <E> Observer<E>.update(e: E) = invoke(ChangeType.UPDATE, e)
suspend fun <E> Observer<E>.delete(e: E) = invoke(ChangeType.DELETE, e)

internal class WatchListObservableRepository<E: Identifiable<ID>, ID>(
    private val delegate: Repository<E, ID>,
): Repository<E, ID> by delegate, ObservableRepository<E, ID> {

    private val observers = mutableListOf<suspend (ChangeType, E) -> Unit>()

    override fun onChange(observer: Observer<E>): Observer<E> {
        observers += observer
        return observer
    }

    override fun forget(observer: Observer<E>) {
        observers.remove(observer)
    }

    override suspend fun create(e: E): E =
        delegate.create(e).also {
            for (observer in observers)
                observer.create(it)
        }

    override suspend fun update(e: E) {
        delegate.update(e).also {
            for (observer in observers)
                observer.update(e)
        }
    }

    override suspend fun delete(id: ID) {
        delegate.delete(id).also {
            val e = get(id) ?: return
            for (observer in observers)
                observer.delete(e)
        }
    }

}