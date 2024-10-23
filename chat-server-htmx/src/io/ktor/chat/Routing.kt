package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.datetime.Clock
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

val admin = User(1, "admin")

fun Application.routingModule() {
    val users by inject<Repository<FullUser, Long>>(named("users"))
    val rooms by inject<Repository<Message, Long>>(named("rooms"))
    val messages by inject<ObservableRepository<Message, Long>>(named("messages"))
    val hashAlgorithm by inject<Algorithm>(named("hash"))

    routing {
        // main chat view
        get {
            val session = call.sessions.get<UserSession>()
            when(session) {
                null -> call.respondRedirect("/login")
                else -> {
                    // TODO remember room
                    val messagesList = messages.list()
                    call.respondHtmlPage {
                        chatRootPage(messagesList)
                    }
                }
            }
        }
        route("/register") {
            post {
                val params = call.receiveParameters()
                val name = params["name"]!!
                val email = params["email"]!!
                val password = params["password"]!!
                val repeat = params["password-repeat"]!!
                val errors = buildList {
                    if (email.isBlank()) add("Email is required")
                    if (password.isBlank()) add("Password is required")
                    if (repeat.isBlank()) add("Password repeat is required")
                    if (password != repeat) add("Passwords do not match")
                }
                environment.log.info("Registration for $email: ${if (errors.isEmpty()) "success" else errors.joinToString()}")
                when {
                    errors.isNotEmpty() -> {
                        call.respondHtmlPage("login-form") {
                            registrationPage(errors)
                        }
                    }
                    else -> {
                        val user = users.create(FullUser(
                            email = email,
                            name = name,
                            password = hashAlgorithm.hash(password))
                        )
                        call.sessions.set(UserSession(user))

                        call.respondRedirect("/")
                    }
                }
            }
            get {
                call.respondHtmlPage("login-form") {
                    registrationPage()
                }
            }
        }
        route("/login") {
            post {
                val params = call.receiveParameters()
                val email = params["email"]!!
                val password = params["password"]!!
                when(val user = users.list {
                    it["email"] = email
                    it["password"] = hashAlgorithm.hash(password)
                }.firstOrNull()) {
                    null -> call.respondHtmlPage("login-form") {
                        loginPage(failure = true)
                    }
                    else -> {
                        call.sessions.set(UserSession(user))
                        call.respondRedirect("/")
                    }
                }
            }
            get {
                call.respondHtmlPage("login-form") {
                    loginPage()
                }
            }
        }
        route("/rooms") {}
        post("/messages") {
            val params = call.receiveParameters()
            val newMessage = Message(
                author = admin,
                text = params["text"]!!,
                created = Clock.System.now(),
                room = params["room"]?.toLongOrNull()!!,
            )
            messages.create(newMessage)

            call.respondText("OK")
        }
    }
}