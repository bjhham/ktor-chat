package io.ktor.chat

import kotlinx.html.*
import kotlin.collections.set

fun FlowContent.chatRootPage(messages: List<Message>) {
    header {
        h1 { +"KTOR CHAT" }
    }
    div {}
    nav {
        +"navigation here"
    }
    main {
        messagesList(messages)
        chatInput()
    }
}

private fun FlowContent.messagesList(messages: List<Message>) {
    if (messages.isEmpty()) {
        div {
            id = "messages-empty"

            +"No messages yet"
        }
    } else {
        div {
            id = "messages-list"

            for (message in messages) {
                div(classes = "message") {
                    div(classes = "message-header") {
                        div(classes = "user-name") {
                            +message.author.name
                        }
                        div(classes = "time") {
                            +message.created.shortened()
                        }
                    }
                    +message.text
                }
            }
        }
    }
}

private fun FlowContent.chatInput() {
    form {
        id = "chat-form"
        attributes["hx-post"] = "/messages"

        input(
            type = InputType.hidden,
            name = "room",
        ) {
            value = "1"
        }
        input(
            type = InputType.text,
            name = "text",
        ) {
            placeholder = "Type something here..."
        }
    }
}