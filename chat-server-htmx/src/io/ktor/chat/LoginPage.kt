package io.ktor.chat

import kotlinx.html.*

fun FlowContent.loginPage(failure: Boolean = false) {
    main {
        h1 { +"KTOR CHAT" }
        div { +"Enter your credentials to continue" }
        form(action = "/login", method = FormMethod.post) {
            input(type = InputType.email, name = "email") {
                placeholder = "Email"
            }
            input(type = InputType.password, name = "password") {
                placeholder = "Password"
            }
            if (failure) {
                div(classes = "error") {
                    +"Wrong email or password"
                }
            }
            div(classes = "spaced-row") {
                a(href = "/register") {
                    +"Register"
                }
                input(type = InputType.submit) {
                    value = "Login"
                }
            }
        }
    }
}

fun FlowContent.registrationPage(errors: List<String> = emptyList()) {
    main {
        h1 { +"KTOR CHAT" }
        div { +"Enter your credentials to continue" }
        form(action = "/register", method = FormMethod.post) {
            input(type = InputType.text, name = "name") {
                placeholder = "Name"
            }
            input(type = InputType.email, name = "email") {
                placeholder = "Email"
            }
            input(type = InputType.password, name = "password") {
                placeholder = "Password"
            }
            input(type = InputType.password, name = "password-repeat") {
                placeholder = "Repeat"
            }
            for (error in errors) {
                div(classes = "error") {
                    +error
                }
            }
            div(classes = "spaced-row") {
                a(href = "/login") {
                    +"Login"
                }
                input(type = InputType.submit) {
                    value = "Register"
                }
            }
        }
    }
}