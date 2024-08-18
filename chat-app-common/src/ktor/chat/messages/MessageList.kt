package ktor.chat.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MessageList(body: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Bottom,
        ) {
        body()
    }
}