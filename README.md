# Ktor Chat

This project provides a full example of a full-stack Ktor application using Compose Multi-platform, Amper, Exposed, 
Koin, and other bleeding-edge Kotlin libraries.

### Project Structure

The project consists of the following six amper modules:

| module                         | description                                                  |
|--------------------------------|--------------------------------------------------------------|
| [core](core)                   | domain data objects, interfaces, and exceptions              |
| [server/db](server/db)         | exposed database schema types and repository implementations |
| [server/common](server/common) | common server functionality                                  |
| [server/rest](server/rest)     | ktor server REST implementation                              |
| [server/htmx](server/htmx)     | HTMX server implementation                                   |
| [server/admin](server/admin)   | admin service for making sensitive changes                   |
| [app/common](app/common)       | shared front-end application code                            |
| [app/android](app/android)     | android front-end application                                |
| [app/desktop](app/desktop)     | jvm desktop front-end application                            |

### Running

There are multiple chat client implementations under `./chat-app-*` that can be executed independently 
with a mock environment, or with a server running.

Here is a list of the commands for running each program:

| application | main source                                         | gradle command                       |
|-------------|-----------------------------------------------------|--------------------------------------|
| server-rest | `server/rest/src/io/ktor/chat/Application.kt`       | `./gradlew :chat-server-rest:run`    |
| server-htmx | `chat-server/src/io/ktor/chat/Application.kt`       | `./gradlew :chat-server-htmx:run`    |
| desktop app | `chat-app-desktop/src/main.kt`                      | `./gradlew :chat-app-desktop:jvmRun` |
| android app | `chat-app-android/src/io/ktor/chat/MainActivity.kt` | `./gradlew :chat-app-android:jvmRun` |