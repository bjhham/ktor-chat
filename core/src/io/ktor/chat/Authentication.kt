package io.ktor.chat

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegistrationRequest(val name: String, val email: String, val password: String)

@Serializable
data class AuthenticationResponse(val token: String, val user: FullUser)