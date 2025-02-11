package io.ktor.chat.server

import io.ktor.chat.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.koin.dsl.module
import io.ktor.util.*
import org.koin.ktor.plugin.koin
import org.slf4j.Logger

fun interface Mailer {
    suspend fun sendEmail(to: String, subject: String, body: String)
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

class MailingUserRepository(
    private val delegate: Repository<FullUser, Long>,
    private val mailer: Mailer,
    private val algorithm: Algorithm,
): Repository<FullUser, Long> by delegate {

    override suspend fun create(e: FullUser): FullUser {
        return delegate.create(e).also { user ->
            with(user) {
                mailer.sendEmail(
                    email,
                    "Welcome to the chat",
                    """
                    You are the newest member of the most exclusive club in town.
                    
                    Here is your activation code:
                    
                    ${algorithm.sign("$id$name$email".toByteArray()).encodeBase64()}
                    
                    Simply pop that baby into your chat app and you're in.
                    """.trimIndent()
                )
            }
        }
    }
}