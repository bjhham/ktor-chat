package ktor.chat.utils

fun String.titleCase() =
    if (isEmpty()) this
    else get(0).uppercaseChar() + substring(1).lowercase()