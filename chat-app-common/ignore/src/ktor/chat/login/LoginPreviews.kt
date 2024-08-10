package ktor.chat.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import ktor.chat.ChatViewModel

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(ChatViewModel())
}