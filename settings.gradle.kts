rootProject.name = "ktor-chat"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/amper/amper")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

plugins {
    id("org.jetbrains.amper.settings.plugin").version("0.5.0-dev-2035")
}

include(":core")

include(":server")
include(":server:db")
include(":server:common")
include(":server:rest")
include(":server:admin")
include(":server:htmx")

include(":client")

include(":app")
include(":app:common")
include(":app:desktop")
include(":app:android")