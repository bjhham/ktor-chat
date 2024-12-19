package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.di.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.rootModule() {
    dependencies {
        provide<Algorithm>("hash") {
            Algorithm.HMAC256(property("security.secret"))
        }
        provide<Mailer> {
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