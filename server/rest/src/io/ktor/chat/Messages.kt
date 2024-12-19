package io.ktor.chat

import io.ktor.di.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.messagesModule() {
    val messages: ObservableRepository<Message, Long> by dependencies

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