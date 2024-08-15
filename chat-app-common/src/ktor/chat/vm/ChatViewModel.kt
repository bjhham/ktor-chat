package ktor.chat.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.ktor.chat.*
import ktor.chat.client.ChatClient
import ktor.chat.client.ChatRestClient
import ktor.chat.client.HttpChatClient
import ktor.chat.client.ServerStatusClient

class ChatViewModel(
    server: String,
    token: String?,
    loggedInUser: User?,
    room: Room?,
    private val client: ChatClient = HttpChatClient(
        server = server,
        token = token,
    ),
): ViewModel(), ServerStatusClient by client, ChatRestClient by client {
    val server = mutableStateOf(server)
    val token = mutableStateOf(token)
    val loggedInUser = mutableStateOf(loggedInUser)
    val room = mutableStateOf(room)
    
    suspend fun login(server: String, email: String, password: String) {
        val authentication = client.login(server, email, password)

        this.server.value = server
        this.token.value = authentication.token
        this.loggedInUser.value = authentication.user
    }
    
    suspend fun register(server: String, email: String, name: String, password: String) {
        client.register(server, email, name, password)
        // TODO do stuff
    }

    suspend fun logout() {
        loggedInUser.value = null
        token.value = null
        client.logout(server.value)
    }

}