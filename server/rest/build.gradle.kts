plugins {
    alias(libs.plugins.ktor)
}

ktor {
    docker {
        imageTag = "ktor-chat-rest"
    }
}