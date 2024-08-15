package ktor.chat.rooms

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import io.ktor.chat.*

@Composable
fun RoomMenuItem(room: Room, selected: Boolean, onSelect: (Room) -> Unit) {
    TextButton(onClick = { onSelect(room) }) {
        Text("#  ${room.name}", fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}