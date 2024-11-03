package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.messagesModule() {
    val messages by inject<ObservableRepository<Message, Long>>(named("messages"))

    routing {
        authenticate {
            route("/messages") {
                restGet(messages)
                restMutations(messages)
                sseChanges(messages)
            }
        }
    }
}