package io.ktor.chat

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.auth() {
    val users by inject<Repository<FullUser, Long>>(named("users"))
    val hashAlgorithm by inject<Algorithm>(named("hash"))
    val audience = environment.config.property("jwt.audience").getString()
    val issuer = environment.config.property("jwt.issuer").getString()

    authentication {
        jwt {
            val verifier = JWT.require(hashAlgorithm)
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
            verifier(verifier)
            validate { credential ->
                ChatPrincipal(
                    credential["id"]?.toLongOrNull() ?: return@validate null,
                    credential["name"] ?: return@validate null
                )
            }
        }
    }

    routing {
        route("/auth") {
            post("login") {
                val credential = call.receive<LoginRequest>()
                val user: FullUser? = users.list { it["email"] = credential.email }.firstOrNull()
                if (user != null && user.password == hashAlgorithm.hash(credential.password)) {
                    val token: String = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("id", user.id.toString())
                        .withClaim("name", user.name)
                        .sign(hashAlgorithm)
                    call.respond(HttpStatusCode.OK, AuthenticationResponse(token, user))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            }
            post("register") {
                val registration = call.receive<RegistrationRequest>()
                val existingUser: FullUser? = users.list().find { it.name == registration.email }
                if (existingUser != null) {
                    call.respond(HttpStatusCode.BadRequest, "User already exists")
                } else {
                    val newUser = FullUser(
                        registration.name,
                        registration.email,
                        hashAlgorithm.hash(registration.password)
                    )
                    val createdUser = users.create(newUser)
                    call.respond(HttpStatusCode.Created, createdUser)
                }
            }
            post("logout") {
                // TODO token cache
                call.respond(HttpStatusCode.OK)
            }
            authenticate {
                get("verify") {
                    val userId =
                        call.principal<ChatPrincipal>()?.user?.id ?: throw BadRequestException("No ID in credentials")
                    val user = users.get(userId) ?: throw BadRequestException("No user found for $userId")
                    call.respondText("Welcome back, ${user.name}", status = HttpStatusCode.OK)
                }
            }
        }
    }
}

@Serializable
data class ChatPrincipal(val user: User): Principal {
    constructor(id: Long, name: String): this(SimplifiedUser(id, name))
}