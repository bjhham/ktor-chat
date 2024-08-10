package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.healthCheckModule() {
    routing {
        get("/ping") {
            call.respondText ("pong")
        }
    }
}