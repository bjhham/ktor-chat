# Ktor Chat

This project provides a full example of a full-stack Ktor application using Compose Multi-platform, Amper, Exposed, Koin, and other bleeding-edge Kotlin libraries.

### Project Structure

The project is comprised of the following six amper modules:

| module                               | description                                                  |
|--------------------------------------|--------------------------------------------------------------|
| [chat-core](chat-core)               | domain data objects, interfaces, and exceptions              |
| [chat-db](chat-db)                   | exposed database schema types and repository implementations |
| [chat-server](chat-server)           | ktor server implementation                                   |
| [chat-app-common](chat-app-common)   | shared front-end code                                        |
| [chat-app-android](chat-app-android) | android front-end application                                |
| [chat-app-desktop](chat-app-desktop) | jvm desktop front-end application                            |