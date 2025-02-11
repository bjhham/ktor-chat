package io.ktor.chat

import io.ktor.client.call.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.testApplication
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagesTest {

    @Test
    fun `CRUD endpoints happy path`() = testApplication {
        configureYaml("auth-config.yaml")
        application {
            root()
            rest()
            mockAuth()
            mockMessagesRepository()
            messages()
        }

        val newMessage = Message(
            author = mockUser,
            room = 1L,
            created = Clock.System.now(),
            text = "Hello, World!",
        )
        val expectedMessage = newMessage.copy(id = 1)

        client.configureForEvents().apply {
            launch(Dispatchers.IO) {
                sse("/messages/changes", {
                    contentType(ContentType.Text.EventStream)
                }) {
                    incoming.collect { event ->
                        println("Event from server: $event")
                    }
                }
            }
        }

        client.configureForTest().apply {
            post("/messages") { setBody(newMessage) }.apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(expectedMessage, body<Message>())
            }
            get("/messages?room=1").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(listOf(expectedMessage), body<List<Message>>())
            }
            
            val updatedMessage = expectedMessage.copy(text = "Brave New World!")
            put("/messages/1") { setBody(updatedMessage)}.apply {
                assertEquals(HttpStatusCode.NoContent, status)
            }
            get("/messages").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(listOf(updatedMessage), body<List<Message>>())
            }
            
            delete("/messages/1").apply {
                assertEquals(HttpStatusCode.NoContent, status)
            }
            get("/messages").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(emptyList(), body<List<Message>>())
            }
        }
    }

}

private fun Application.mockMessagesRepository() {
    koin {
        modules(module {
            single<ObservableRepository<Message, Long>>(named("messages")) {
                ListRepository.create<Message>().observable()
            }
        })
    }
}