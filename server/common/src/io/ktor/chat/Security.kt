package io.ktor.chat

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.*

fun Algorithm.hash(input: String): String =
    sign(input.toByteArray()).encodeBase64()