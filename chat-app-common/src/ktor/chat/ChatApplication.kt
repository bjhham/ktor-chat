package ktor.chat

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ktor.chat.login.LoginScreen

@Composable
fun ChatApplication(vm: ChatViewModel = viewModel()) {
    if (vm.me == null)
        LoginScreen(vm)
    else
        ChatScreen(vm)
}
