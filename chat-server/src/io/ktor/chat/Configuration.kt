package io.ktor.chat

import io.ktor.server.application.*

fun Application.property(key: String) =
    environment.config.property(key).getString()