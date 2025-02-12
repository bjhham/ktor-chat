package io.ktor.chat.server

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import org.slf4j.Logger

fun interface Mailer {
    suspend fun sendEmail(recipient: String, subject: String, body: String)
}

fun Application.mail() {
    koin {
        modules(module {
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
}

fun Logger.logMailer() =
    Mailer { to, subject, body ->
        info("""
        |SENT EMAIL
        |    to: $to
        |    subject: $subject
        |    body:\n${body.prependIndent("    ")}
        |""".trimMargin())
    }