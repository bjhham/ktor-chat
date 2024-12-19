package io.ktor.chat

import io.ktor.di.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.usersModule() {
    val users: Repository<FullUser, Long> by dependencies

    routing {
        authenticate {
            route("/users") {
                get {
                    call.respond(users.list().map {
                        SimplifiedUser(it.id, it.name)
                    })
                }
            }
        }
    }
}