import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ktor.chat.ChatApplication

fun main() = application {
    Window(title = "KTOR CHAT", onCloseRequest = ::exitApplication) {
        ChatApplication()
    }
}