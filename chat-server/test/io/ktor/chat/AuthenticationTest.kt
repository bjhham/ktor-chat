package io.ktor.chat

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AuthenticationTest {

    @Test
    fun `registration and login works`() = authenticationTest {
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                  "name": "Joey Bloggs",
                  "email": "joey@example.com",
                  "password": "password123"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        val token = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                    {
                      "email": "joey@example.com",
                      "password": "password123"
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }.bodyAsText()

        client.get("/users") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "Joey Bloggs", message = "Should contain newly registered user")
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
            Application::authModule,
            Application::usersModule,
            configFile = "auth-config.yaml",
        ){
            block()
        }

}