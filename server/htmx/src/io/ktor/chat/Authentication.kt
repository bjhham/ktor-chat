package io.ktor.chat

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

fun Application.sessionCookieModule() {
    install(Sessions) {
        // TODO store in database
        cookie<UserSession>("session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAge = 30.days
        }
    }
}

@Serializable
data class UserSession(val user: FullUser)