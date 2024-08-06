package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.*
import org.slf4j.Logger

fun interface Mailer {
    suspend fun sendEmail(to: String, subject: String, body: String)
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
    private val delegate: Repository<User, Long>,
    private val mailer: Mailer,
    private val algorithm: Algorithm,
): Repository<User, Long> by delegate {

    override suspend fun create(e: User): User {
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