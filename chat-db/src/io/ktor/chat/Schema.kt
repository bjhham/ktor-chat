package io.ktor.chat

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Users : LongIdTable() {
    val name = varchar("name", length = 42)
    val email = varchar("email", length = 128)
    val password = varchar("password", length = 128)
}

object Rooms : LongIdTable() {
    val name: Column<String> = varchar("name", length = 42)
}

object Messages : LongIdTable() {
    val author = reference("author", Users)
    val created = timestamp("created")
    val room = long("room").references(Rooms.id)
    val text = text("text")
    val modified = timestamp("modified").nullable()
}