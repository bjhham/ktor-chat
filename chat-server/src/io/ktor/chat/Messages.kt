package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.messagesModule() {
    val messages by inject<ObservableRepository<Message, Long>>(named("messages"))

    routing {
        authenticate {
            route("/messages") {
                restMethods(messages)
                sseChanges(messages)
            }
        }
    }
}