package ktor.chat

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import ktor.chat.client.MockChatClient

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(ChatViewModel(MockChatClient()))
}