package io.ktor.chat.server

import io.ktor.chat.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.util.*

fun Application.rest() {
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<ConflictingArgumentException> { call, ex ->
            call.respondText(ContentType.Text.Plain, HttpStatusCode.Conflict) {
                ex.message ?: "Duplicate value!"
            }
        }
        exception<IllegalArgumentException> { call, ex ->
            call.respondText(ContentType.Text.Plain, HttpStatusCode.BadRequest) {
                ex.message ?: "Bad input!"
            }
        }
        exception<Exception> { call, ex ->
            this@rest.environment.log.error("Internal server error, returning 500", ex)
            call.respondText(ContentType.Text.Plain, HttpStatusCode.InternalServerError) {
                "Internal error!"
            }
        }
    }
    install(SSE)
}

inline fun <reified E: Identifiable<Long>> Route.restGet(
    repository: Repository<E, Long>
) {
    get {
        val query = MapQuery.of(call.queryParameters.toMap())
        call.respond(repository.list(query))
    }
}

inline fun <reified E: Identifiable<Long>> Route.restMutations(
    repository: Repository<E, Long>
) {
    post {
        val newEntity = repository.create(call.receive())
        call.respond(newEntity)
    }
    put("{id}") {
        val entity = call.receive<E>()
        repository.update(entity)

        call.respond(HttpStatusCode.NoContent)
    }
    delete("{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: throw NotFoundException()
        repository.delete(id)

        call.respond(HttpStatusCode.NoContent)
    }
}