package io.ktor.chat

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class UserRepository(database: Database): ExposedRepository<User, Long, Users>(database, Users) {
    override fun rowToEntity(row: ResultRow): User =
        User(
            name = row[Users.name],
            email = row[Users.email],
            password = row[Users.password],
            id = row[Users.id].value,
        )

    override fun User.withId(id: Long): User =
        copy(id = id)

    override fun assignColumns(e: User): Users.(UpdateBuilder<*>) -> Unit = {
        it[name] = e.name
        it[email] = e.email
        it[password] = e.password
    }

}