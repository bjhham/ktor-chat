package ktor.chat.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun CreateRoomDialog(
    onCreate: suspend (String) -> Unit,
    onClose: () -> Unit
) {
    var name by mutableStateOf("")
    val coroutineScope = rememberCoroutineScope()
    
    AlertDialog(
        title = { Text("Create room") },
        onDismissRequest = { onClose() },
        text = {
            Column {
                TextField(
                    value = name,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { name = it },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        onCreate(name)
                        onClose()
                    }
                },
                enabled = name.isNotBlank(),
            ) {
                Text("Save")
            }
        }
    )
}