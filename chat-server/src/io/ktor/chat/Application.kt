package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sse.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.rootModule() {
    install(Koin) {
        slf4jLogger()
        modules(module(createdAtStart = true) {
            val hmac256 = Algorithm.HMAC256(property("security.secret"))
            
            single<Algorithm>(named("hash")) { hmac256 }
            
            single<Mailer> {
                Mailer { to, subject, body ->
                    log.info("""
                        |SENT EMAIL
                        |    to: $to
                        |    subject: $subject
                        |    body:\n${body.prependIndent("    ")}
                        |""".trimMargin())
                }
            }
        })
    }
    install(ContentNegotiation) {
        json()
    }
    install(SSE)
}

fun Application.property(key: String) =
    environment.config.property(key).getString()