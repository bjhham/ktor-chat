package ktor.chat.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.chat.*

@Composable
actual fun createViewModel(): ChatViewModel {
    return viewModel {
        ChatViewModel(
            server = "http://localhost:8080",
            token = null,
            loggedInUser = null,
            room = null,
        )
    } // TODO save state
}