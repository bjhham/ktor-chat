package ktor.chat.client

import io.ktor.chat.*

interface ChatClient :
        AuthClient,
        ServerStatusClient,
        ChatRestClient

interface AuthClient {
    suspend fun login(server: String, email: String, password: String): AuthenticationResponse
    suspend fun register(server: String, email: String, name: String, password: String)
}

interface ServerStatusClient {
    suspend fun isServerAvailable(server: String): Boolean
}

interface ChatRestClient {
    val rooms: Repository<Room, Long>
    val messages: ObservableRepository<Message, Long>
    
    val users: ReadOnlyRepository<Named, Long>
}