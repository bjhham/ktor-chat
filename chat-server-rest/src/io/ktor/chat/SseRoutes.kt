package io.ktor.chat

import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified E: Identifiable<Long>> Route.sseChanges(observableRepository: ObservableRepository<E, Long>) {
    sse("changes") {
        val observer = observableRepository.onChange { type, e ->
            send(ServerSentEvent("${type.name} ${Json.encodeToString(e)}"))
        }
        try {
            awaitCancellation()
        } finally {
            observableRepository.forget(observer)
        }
    }
}