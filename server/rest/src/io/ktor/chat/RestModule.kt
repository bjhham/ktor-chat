package io.ktor.chat

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sse.*

fun Application.restModule() {
    install(ContentNegotiation) {
        json()
    }
    install(SSE)
}