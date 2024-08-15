package ktor.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.chat.*
import kotlinx.datetime.Clock
import ktor.chat.client.listInRoom
import ktor.chat.client.remoteList
import ktor.chat.components.RemoteLoader
import ktor.chat.messages.MessageInput
import ktor.chat.messages.MessageList
import ktor.chat.messages.MessageListItem
import ktor.chat.rooms.AddRoomButton
import ktor.chat.rooms.RoomMenuItem
import ktor.chat.rooms.RoomsMenu
import ktor.chat.utils.*
import ktor.chat.vm.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    var selectedRoom by remember { vm.room }
    val roomsRemote: Remote<List<Room>> by vm.rooms.remoteList()
    val createdRooms = remember { mutableStateListOf<Room>() }
    val messagesRemote: Remote<SnapshotStateList<Message>> by vm.messages.listInRoom(selectedRoom)
    
    ChatLayout(rooms = {
        RemoteLoader(roomsRemote) { rooms ->
            RoomsMenu {
                LazyColumn {
                    items(rooms + createdRooms) { room ->
                        RoomMenuItem(room, selected = room == selectedRoom) {
                            selectedRoom = it
                        }
                    }
                }
                AddRoomButton { newRoomName ->
                    createdRooms += vm.rooms.create(Room(newRoomName))
                }
            }
        }
    }) {
        when(selectedRoom) {
            null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Select a room to begin")
            }
            else -> RemoteLoader(messagesRemote) { messages ->
                MessageList {
                    when(messages.size) {
                        0 -> Text("Nothing here yet...")
                        else -> LazyColumn {
                            items(messages) { message ->
                                MessageListItem(message)
                            }
                        }
                    }
                    MessageInput { messageText ->
                        vm.messages.create(
                            Message(
                                author = vm.loggedInUser.value!!,
                                created = Clock.System.now(),
                                room = selectedRoom!!.id,
                                text = messageText,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatLayout(
    rooms: @Composable () -> Unit,
    messages: @Composable () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.width(200.dp)) {
            rooms()
        }
        messages()
    }
}