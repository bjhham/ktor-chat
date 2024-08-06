package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.usersModule() {
    val users by inject<Repository<User, Long>>(named("users"))

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

@Serializable
data class SimplifiedUser(val id: Long, val name: String)