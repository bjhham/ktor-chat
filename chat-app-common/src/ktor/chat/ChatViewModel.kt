package ktor.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.chat.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ktor.chat.client.ChatClient
import ktor.chat.client.ChatRestClient
import ktor.chat.client.HttpChatClient
import ktor.chat.client.ServerStatusClient

class ChatViewModel(
    private val client: ChatClient = HttpChatClient()
): ViewModel(),
   ServerStatusClient by client,
   ChatRestClient by client {
    var loading by mutableStateOf(false)
    var token by mutableStateOf<String?>(null)
    var me by mutableStateOf<User?>(null)
    
    suspend fun login(server: String, email: String, password: String) {
        val authentication = client.login(server, email, password)
        token = authentication.token
        me = authentication.user
    }
    
    suspend fun register(server: String, email: String, name: String, password: String) {
        client.register(server, email, name, password)
        // TODO do stuff
    }
    
}