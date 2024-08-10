package ktor.chat.client

import io.ktor.chat.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class MockChatClient(
    rootUser: FullUser = FullUser("Steve", "steve@mail.com", "kek")
): ChatClient {
    
    override suspend fun login(server: String, email: String, password: String): AuthenticationResponse =
        users.list().find {
            it.email == email && it.password == password
        }?.let {
            AuthenticationResponse("abc123def456", it)
        } ?: throw ResponseFailureException(HttpStatusCode.Unauthorized, "Your creds don't work")

    override suspend fun register(server: String, email: String, name: String, password: String) {
        users.create(FullUser(name, email, password))
    }

    override suspend fun isServerAvailable(server: String): Boolean = true
    
    override val users = ListRepository.create(
        rootUser
    ) { user, id ->
        user.copy(id = id)
    }

    override val rooms = ListRepository.create(
        Room("lobby")
    ) { room, id ->
        room.copy(id = id)
    }
    override val messages = ListRepository.create(
        Message(
            author = rootUser,
            room = 1,
            created = Clock.System.now()
                .minus(42, DateTimeUnit.SECOND),
            text = "Hello, World!"
        )
    ) { message, id ->
        message.copy(id = id)
    }.observable()
    
}