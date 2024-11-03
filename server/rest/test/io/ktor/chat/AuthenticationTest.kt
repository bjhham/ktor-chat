package io.ktor.chat

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AuthenticationTest {

    @Test
    fun `registration and login works`() = authenticationTest {
        client.config {
            install(ContentNegotiation) {
                json()
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
        }.apply {
            post("/auth/register") {
                setBody(RegistrationRequest(
                    "Joey Bloggs",
                    "joey@example.com",
                    "password123",
                ))
            }.apply {
                assertEquals(HttpStatusCode.Created, status)
            }

            val authResponse = post("/auth/login") {
                setBody(LoginRequest(
                    "joey@example.com",
                    "password123",
                ))
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
            }.body<AuthenticationResponse>()

            get("/users") {
                header(HttpHeaders.Authorization, "Bearer ${authResponse.token}")
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                assertContains(bodyAsText(), "Joey Bloggs", message = "Should contain newly registered user")
            }
        }
    }

    @Test
    fun `login fails with missing user`() = authenticationTest {
        client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                  "email": "not.a.user@example.com",
                  "password": "password123"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

    private fun authenticationTest(block: suspend ApplicationTestBuilder.() -> Unit) =
        testApplicationWith(
            Application::rootModule,
            Application::restModule,
            Application::mockUsersRepository,
            Application::authModule,
            Application::usersModule,
            configFile = "auth-config.yaml",
        ){
            block()
        }

}

private fun Application.mockUsersRepository() {
    koin {
        modules(module {
            single<Repository<FullUser, Long>>(named("users")) {
                ListRepository.create()
            }
        })
    }
}