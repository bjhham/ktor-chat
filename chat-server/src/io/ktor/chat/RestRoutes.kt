package io.ktor.chat

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

inline fun <reified E: Identifiable<Long>> Routing.restMethods(
    repository: Repository<E, Long>
) {
    get {
        val query = MapQuery(call.queryParameters.toMap())
        call.respond(repository.list(query))
    }
    post {
        call.respond(HttpStatusCode.Created, repository.create(call.receive()))
    }
    put("{id}") {
        val entity = call.receive<E>()
        repository.update(entity)
        call.respond(HttpStatusCode.Accepted)
    }
    delete("{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: throw NotFoundException()
        repository.delete(id)
        call.respond(HttpStatusCode.Accepted)
    }

}