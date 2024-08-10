import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ktor.chat.ChatApplication
import ktor.chat.ChatViewModel
import ktor.chat.client.MockChatClient

fun main() = application {
    Window(title = "KLONE CHAT", onCloseRequest = ::exitApplication) {
        ChatApplication(ChatViewModel(MockChatClient()))
    }
}