package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

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
    install(ContentNegotiation) {
        register(ContentType.Text.Html, HtmlContentConverter())
    }
}

class HtmlContentConverter: ContentConverter {
    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        contentType.parameter("view")
        TODO("Not yet implemented")
    }

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel
    ): Any? {
        error("Unsupported")
    }
}