package ktor.chat.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.chat.*

@Composable
fun RoomsMenu(
    asDropdown: Boolean = false,
    rooms: List<Room>,
    selectedRoom: Room?,
    onSelect: (Room) -> Unit,
    onCreate: suspend (String) -> Unit,
    content: @Composable () -> Unit,
) {
    var addRoomDialogExpanded by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    @Composable
    fun RoomsNavigation() {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp, 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            rooms.forEach { room ->
                NavigationDrawerItem(
                    selected = room.id == selectedRoom?.id,
                    onClick = { onSelect(room) },
                    icon = { Icon(imageVector = Icons.Filled.Group, contentDescription = null) },
                    label = { Text(room.name) }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(4.dp))
            NavigationDrawerItem(
                selected = false,
                onClick = { addRoomDialogExpanded = true },
                icon = { Icon(imageVector = Icons.Filled.GroupAdd, contentDescription = null) },
                label = { Text("Create") }
            )
        }
    }

    if (asDropdown) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    RoomsNavigation()
                }
            },
            content = content
        )
    } else {
        PermanentNavigationDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerContent = {
                PermanentDrawerSheet {
                    RoomsNavigation()
                }
            },
            content = content
        )
    }


    if (addRoomDialogExpanded)
        AddRoomDialog(onCreate) { addRoomDialogExpanded = false }
}