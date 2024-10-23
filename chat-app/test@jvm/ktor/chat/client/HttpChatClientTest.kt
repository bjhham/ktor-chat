package ktor.chat.client

import io.ktor.chat.*
import kotlin.test.Test

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.yaml.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.datetime.Clock
import ktor.chat.client.HttpChatClient.Companion.configureForChat
import kotlin.test.Ignore

class HttpChatClientTest {
    
    @Ignore
    @Test
    fun endToEndTest() = testApplication {
        environment {
            config = YamlConfig("test-server.yaml")!!
        }
        
        val serverUrl = "http://localhost"
        var testUser = FullUser(
            "bob",
            "bob@law.blog",
            "pa55word"
        )
        
        HttpChatClient(client.configureForChat()).apply {
            register(serverUrl, testUser.email, testUser.name, testUser.password)
            testUser = login(serverUrl, testUser.email, testUser.password).user
            val room = rooms.create(Room("lobby"))
            val message = messages.create(
                Message(
                    author = testUser,
                    room = room.id,
                    created = Clock.System.now(),
                    text = "Hello, world!"
                )
            )
            messages.delete(message.id)
            rooms.delete(room.id)
        }
    }
    
}