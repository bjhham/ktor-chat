plugins {
//    alias(libs.plugins.shadow)
    alias(libs.plugins.ktor)
}

ktor {
    docker {
        imageTag = "ktor-chat-rest"
    }
}