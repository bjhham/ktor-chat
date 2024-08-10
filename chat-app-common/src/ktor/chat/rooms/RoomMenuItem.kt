package ktor.chat.rooms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.chat.*

@Composable
fun RoomMenuItem(room: Room) {
    Text(
        "#  ${room.name}",
        modifier = Modifier.padding(4.dp, 8.dp),
    )
}