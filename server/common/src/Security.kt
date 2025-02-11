package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.util.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.koin

fun Application.security() {
    koin {
        modules(module {
            single<Algorithm>(named("hash")) {
                Algorithm.HMAC256(
                    environment.config.property("security.secret").getString()
                )
            }
        })
    }
}

fun Algorithm.hash(input: String): String =
    sign(input.toByteArray()).encodeBase64()