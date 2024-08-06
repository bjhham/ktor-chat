package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.roomsModule() {
    val rooms by inject<Repository<Room, Long>>(named("rooms"))

    routing {
        authenticate {
            route("/rooms") {
                restMethods(rooms)
            }
        }
    }
}