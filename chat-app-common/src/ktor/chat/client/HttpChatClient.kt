package ktor.chat.client

import io.ktor.chat.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import ktor.chat.client.ResponseFailureException.Companion.throwIfNotOk

class HttpChatClient: ChatClient {
    private var http = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }
    
    override suspend fun login(server: String, email: String, password: String): AuthenticationResponse {
        val authentication = http.post("$server/auth/login") {
            setBody(LoginRequest(email, password))
        }.throwIfNotOk().body<AuthenticationResponse>()
        
        http = http.config {
            install(DefaultRequest) {
                url(server)
                header(HttpHeaders.Authorization, "Bearer ${authentication.token}")
            }
        }
        return authentication
    }
    
    override suspend fun register(server: String, email: String, name: String, password: String) {
        http.post("$server/auth/register") {
            setBody(RegistrationRequest(name, email, password))
        }.throwIfNotOk()
    }
    
    override suspend fun isServerAvailable(server: String): Boolean {
        return http.get("$server/ping").let { response ->
            if (response.status.isSuccess())
                response.bodyAsText() == "pong"
            else false
        }
    }

    override val rooms: Repository<Room, Long> =
        restRepository(http, "rooms")
    
    override val messages: ObservableRepository<Message, Long> =
        observableRepository(http, "messages")

    override val users: ReadOnlyRepository<SimplifiedUser, Long> =
        restRepository(http, "users")
}

inline fun <reified E: Identifiable<ID>, ID> restRepository(
    http: HttpClient,
    path: String,
) = object : Repository<E, ID> {
    
    override suspend fun get(id: ID): E? =
        http.get("$path/$id").throwIfNotOk().body()

    override suspend fun create(e: E): E =
        http.post(path).throwIfNotOk().body()

    override suspend fun update(e: E) {
        http.put(path).throwIfNotOk()
    }

    override suspend fun delete(id: ID) {
        http.delete("$path/$id").throwIfNotOk()
    }
    
    override suspend fun list(query: Query): List<E> =
        http.get(path) {
            if (query is MapQuery)
                parametersOf(query)
        }.throwIfNotOk().body()

}

inline fun <reified E : Identifiable<ID>, ID> observableRepository(
    http: HttpClient,
    path: String,
): ObservableRepository<E, ID> =
    object : Repository<E, ID> by restRepository(http, path), ObservableRepository<E, ID> {
        override fun onChange(observer: Observer<E>): Observer<E> {
            TODO("Not yet implemented")
        }
        override fun forget(observer: Observer<E>) {
            TODO("Not yet implemented")
        }
    
    }

class ResponseFailureException(status: HttpStatusCode, message: String) : RuntimeException(buildString {
    append(status.description)
    if (message.isNotBlank())
        append(": ").append(message)
}) {
    companion object {
        suspend fun HttpResponse.throwIfNotOk(): HttpResponse =
            also {
                if (!status.isSuccess())
                    throw asException()
            }
        
        suspend fun HttpResponse.asException() =
            ResponseFailureException(status, bodyAsText())
    }
}