package io.ktor.chat

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface Identifiable<ID> {
    val id: ID
}

@Serializable
data class User(
    val name: String,
    val email: String,
    val password: String,
    override val id: Long = 0,
): Identifiable<Long>

@Serializable
data class Room(
    val name: String,
    override val id: Long = 0,
): Identifiable<Long>

@Serializable
data class Message(
    val author: User,
    val created: Instant,
    val text: String,
    override val id: Long = 0,
    val modified: Instant? = null,
): Identifiable<Long>