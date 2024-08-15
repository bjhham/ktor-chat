# Ktor Chat

This project provides a full example of a full-stack Ktor application using Compose Multi-platform, Amper, Exposed, 
Koin, and other bleeding-edge Kotlin libraries.

### Project Structure

The project consists of the following six amper modules:

| module                               | description                                                  |
|--------------------------------------|--------------------------------------------------------------|
| [chat-core](chat-core)               | domain data objects, interfaces, and exceptions              |
| [chat-db](chat-db)                   | exposed database schema types and repository implementations |
| [chat-server](chat-server)           | ktor server implementation                                   |
| [chat-app-common](chat-app-common)   | shared front-end code                                        |
| [chat-app-android](chat-app-android) | android front-end application                                |
| [chat-app-desktop](chat-app-desktop) | jvm desktop front-end application                            |

### Running

There are multiple chat client implementations under `./chat-app-*` that can be executed independently 
with a mock environment, or with a server running.

Here is a list of the commands for running each program:

| application | main source                                         | gradle command                    |
|-------------|-----------------------------------------------------|-----------------------------------|
| server      | `chat-server/src/io/ktor/chat/Application.kt`       | `./gradlew :chat-server:run`      |
| desktop app | `chat-app-desktop/src/main.kt`                      | `./gradlew :chat-app-desktop:run` |
| android app | `chat-app-android/src/io/ktor/chat/MainActivity.kt` | `./gradlew :chat-app-android:run` |