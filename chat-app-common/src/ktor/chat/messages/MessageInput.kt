package ktor.chat.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MessageInput(send: suspend (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }
    
    // TODO loading feedback
    fun sendMessage() {
        val messageToSend = message
        coroutineScope.launch {
            send(messageToSend.trim())
        }
        message = ""
    }
    
    Spacer(Modifier.height(10.dp))
    Box {
        TextField(
            value = message,
            placeholder = { Text("Type a message...") },
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth().onKeyEvent { e ->
                when (e.key.keyCode) {
                    Key.Enter.keyCode -> {
                        if (!e.isShiftPressed)
                            sendMessage()
                        true
                    }
                    else -> false
                }
            }
        )
        IconButton(
            onClick = { sendMessage() },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Send message"
            )
        }
    }
}