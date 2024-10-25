package io.ktor.chat

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface Identifiable<ID> {
    val id: ID
}

@Serializable
sealed interface Named: Identifiable<Long> {
    val name: String
}

typealias User = Named

fun User(id: Long, name: String) =
    SimplifiedUser(id, name)

@Serializable
data class FullUser(
    override val name: String,
    val email: String,
    val password: String,
    override val id: Long = 0,
): Named


@Serializable
data class SimplifiedUser(
    override val id: Long,
    override val name: String
): Named

@Serializable
data class Room(
    val name: String,
    override val id: Long = 0,
): Identifiable<Long>

@Serializable
data class Message(
    val author: User,
    val room: Long,
    val created: Instant,
    val text: String,
    override val id: Long = 0,
    val modified: Instant? = null,
): Identifiable<Long>