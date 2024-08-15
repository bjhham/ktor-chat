package ktor.chat.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import ktor.chat.vm.createViewModel

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(createViewModel())
}