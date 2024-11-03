package io.ktor.chat

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import java.nio.file.Paths
import kotlin.io.path.exists

fun Application.databaseModule() {
    val mode = if (Paths.get("module.yaml").exists()) "test" else "main"
    log.info("Using database: $mode")
    val database = Database.connect(
        url = property("database.$mode.url"),
        user = property("database.$mode.user"),
        driver = property("database.$mode.driver"),
        password = property("database.$mode.password")
    ).also { db ->
        transaction(db) {
            SchemaUtils.create(Users, Rooms, Messages, Members)
        }
    }
    koin {
        modules(module {
            // Note: qualifier is required because generics don't work with Koin
            single<Database> { database }
        })
    }
}