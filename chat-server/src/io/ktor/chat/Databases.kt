package io.ktor.chat

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.koin

fun Application.databaseModule() {
    val database = Database.connect(
        url = property("database.main.url"),
        user = property("database.main.user"),
        driver = property("database.main.driver"),
        password = property("database.main.password")
    ).also { db ->
        transaction(db) {
            SchemaUtils.create(Users, Rooms, Messages)
        }
    }
    koin {
        modules(module {
            // Note: qualifier is required because generics don't work with Koin
            single<Database> { database }
        })
    }
}