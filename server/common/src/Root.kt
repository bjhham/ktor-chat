package io.ktor.chat.server

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.root() {
    install(Koin) {
        slf4jLogger()
    }
}