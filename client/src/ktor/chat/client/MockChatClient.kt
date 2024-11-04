package ktor.chat.client

import io.ktor.chat.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class MockChatClient(
    rootUser: FullUser = FullUser("Steve", "steve@mail.com", "kek"),
    lobby: Room = Room("lobby", id = 1)
): ChatClient {
    override suspend fun verify(): Boolean = true

    override suspend fun login(server: String, email: String, password: String): AuthenticationResponse =
        users.list().find {
            it.email == email && it.password == password
        }?.let {
            AuthenticationResponse("abc123def456", it)
        } ?: throw IllegalAccessException()

    override suspend fun register(server: String, email: String, name: String, password: String) {
        users.create(FullUser(name, email, password))
    }

    override suspend fun isServerAvailable(server: String): Boolean = true

    override suspend fun logout(server: String) {
        // no sessions here yet
    }

    override val users = ListRepository.create(
        rootUser
    )

    override val rooms = ListRepository.create(lobby)
    override val messages = ListRepository.create(
        Message(
            author = rootUser,
            room = lobby.id,
            created = Clock.System.now()
                .minus(42, DateTimeUnit.SECOND),
            text = "Hello, World!"
        )
    ).observable()
    override val memberships: ObservableRepository<Membership, Long> = ListRepository.create<Membership>().observable()
}