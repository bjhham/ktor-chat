package ktor.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ktor.chat.login.LoginScreen
import ktor.chat.vm.ChatViewModel
import ktor.chat.vm.createViewModel

@Composable
fun ChatApplication(
    vm: ChatViewModel = createViewModel()
) {
    val loggedInUser by vm.loggedInUser

    if (loggedInUser == null)
        LoginScreen(vm)
    else
        ChatScreen(vm)
}
