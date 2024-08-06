package io.ktor.chat

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.authModule() {
    val users by inject<Repository<User, Long>>(named("users"))
    val hashAlgorithm by inject<Algorithm>(named("hash"))
    val audience = property("jwt.audience")
    val issuer = property("jwt.issuer")

    authentication {
        jwt {
            val verifier = JWT.require(hashAlgorithm)
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
            verifier(verifier)
            validate { credential ->
                credential["id"]
                    ?.toLongOrNull()
                    ?.let { users.get(it) }
                    ?.let(::ChatPrincipal)
            }
        }
    }

    routing {
        route("/auth") {
            post("login") {
                val credential = call.receive<Credential>()
                val user: User? = users.list().find { it.email == credential.email }
                if (user != null && user.password == hashAlgorithm.hash(credential.password)) {
                    val token: String = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("id", user.id.toString())
                        .sign(hashAlgorithm)
                    call.respond(HttpStatusCode.OK, token)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            }
            post("register") {
                val registration = call.receive<Registration>()
                val existingUser: User? = users.list().find { it.name == registration.email }
                if (existingUser != null) {
                    call.respond(HttpStatusCode.BadRequest, "User already exists")
                } else {
                    val newUser = User(
                        registration.name,
                        registration.email,
                        hashAlgorithm.hash(registration.password)
                    )
                    val createdUser = users.create(newUser)
                    call.respond(HttpStatusCode.Created, createdUser)
                }
            }
        }
    }
}

@Serializable
data class Credential(val email: String, val password: String)

@Serializable
data class Registration(val name: String, val email: String, val password: String)

@Serializable
data class ChatPrincipal(val user: User): Principal