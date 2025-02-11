package io.ktor.chat.server

import io.ktor.chat.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.koin

fun Application.repositories() {
    val database by inject<Database>()
    val hmac256 by inject<Algorithm>(named("hash"))
    
    koin {
        modules(module {
            // Note: qualifier is required because generics don't work with Koin
            single<Repository<FullUser, Long>>(named("users")) {
                MailingUserRepository(
                    delegate = UserRepository(database),
                    mailer = environment.log.logMailer(),
                    algorithm = hmac256,
                )
            }
            single<ObservableRepository<Message, Long>>(named("messages")) {
                MessageRepository(database).observable(onFailure = { e ->
                    // TODO cancel SSE
                    environment.log.error("Failed to subscribe to event", e)
                })
            }
            single<Repository<Room, Long>>(named("rooms")) {
                RoomRepository(database)
            }
            single<ObservableRepository<Membership, Long>>(named("members")) {
                MemberRepository(database).observable(onFailure = { e ->
                    // TODO cancel SSE
                    environment.log.error("Failed to subscribe to event", e)
                })
            }
        })
    }
}