package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.sse.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun Application.rootModule() {
    install(Koin) {
        slf4jLogger()
        modules(module(createdAtStart = true) {
            val hmac256 = Algorithm.HMAC256(property("security.secret"))

            single<Algorithm>(named("hash")) { hmac256 }

            single<Mailer> {
                Mailer { to, subject, body ->
                    log.info(
                        """
                        |SENT EMAIL
                        |    to: $to
                        |    subject: $subject
                        |    body:\n${body.prependIndent("    ")}
                        |""".trimMargin()
                    )
                }
            }
        })
    }
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val uri = call.request.uri
            val timeTaken = call.processingTimeMillis()
            "$status | ${timeTaken}ms | $httpMethod $uri"
        }
    }
}