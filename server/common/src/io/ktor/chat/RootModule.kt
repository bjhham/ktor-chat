package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.di.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.koin.core.qualifier.named
import org.koin.ktor.plugin.Koin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun Application.rootModule() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            single<Algorithm>(named("hash")) {
                Algorithm.HMAC256(property("security.secret"))
            }
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
    install(DI) {

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