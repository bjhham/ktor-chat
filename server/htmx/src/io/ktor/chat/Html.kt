package io.ktor.chat

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.consumers.filter
import kotlinx.html.stream.appendHTML

val htmlContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8)

suspend fun RoutingCall.respondHtmlPage(classes: String? = null, block: FlowContent.() -> Unit) {
    respondText(
        contentType = htmlContentType,
        text = buildString {
            append("<!DOCTYPE html>\n")
            appendHTML().html {
                head {
                    lang = "en"
                    importResource("head.html")
                    style {
                        importResource("style.css")
                    }
                }
                body(classes) {
                    block()
                }
            }
        }
    )
}

suspend fun RoutingCall.respondHtml(status: HttpStatusCode = HttpStatusCode.OK, block: BODY.() -> Unit) {
    respondText(
        status = status,
        contentType = htmlContentType,
        text = buildString {
            appendHTML().filter {
                if (it.tagName in listOf("html", "body")) SKIP else PASS
            }.html {
                body {
                    block(this)
                }
            }
        }
    )
}

fun HTMLTag.importResource(filename: String) {
    val content = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)?.bufferedReader()
        ?: throw RuntimeException("Resource not found $filename")
    content.use { reader ->
        unsafe {
            raw(reader.readText())
        }
    }
}