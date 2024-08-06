package io.ktor.chat

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

inline fun <reified E: Identifiable<Long>> Routing.restMethods(repository: Repository<E, Long>) {
    get {
        call.respond(repository.list())
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