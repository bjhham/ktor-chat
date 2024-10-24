package ktor.chat.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ktor.chat.utils.Done
import ktor.chat.utils.Error
import ktor.chat.utils.Loading
import ktor.chat.utils.Remote

@Composable
fun <T> RemoteLoader(remote: Remote<T>, handleResult: @Composable (T) -> Unit) {
    when(remote) {
        is Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        
        }
        is Error -> {
            ErrorText(remote.message)
        }
        is Done -> {
            handleResult(remote.value)
        }
    }
}