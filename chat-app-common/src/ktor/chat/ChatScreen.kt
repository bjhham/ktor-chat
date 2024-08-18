package ktor.chat

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ktor.chat.rooms.RoomsMenu
import ktor.chat.utils.Remote
import ktor.chat.vm.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    var selectedRoom by remember { vm.room }
    val roomsRemote: Remote<List<Room>> by vm.rooms.remoteList()
    val createdRooms = remember { mutableStateListOf<Room>() }
    val messagesRemote: Remote<SnapshotStateList<Message>> by vm.messages.listInRoom(selectedRoom)
    val smallScreen by remember { derivedStateOf { vm.screenSize.value.first < 1400 } }

    RemoteLoader(roomsRemote) { rooms ->
        RoomsMenu(
            asDropdown = smallScreen,
            rooms = rooms + createdRooms,
            selectedRoom = selectedRoom,
            onSelect = {
                selectedRoom = it
            },
            onCreate = {
                newRoomName ->
                createdRooms += vm.rooms.create(Room(newRoomName))
            }
        ) {
            when(selectedRoom) {
                null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a room to begin")
                }
                else -> RemoteLoader(messagesRemote) { messages ->
                    MessageList {
                        when(messages.size) {
                            0 -> Text("Nothing here yet...", modifier = Modifier.padding(10.dp, 5.dp))
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
}

//@Composable
//fun ChatLayout(
//    smallScreen: Boolean,
//    rooms: @Composable () -> Unit,
//    messages: @Composable () -> Unit,
//) {
//    if (smallScreen) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            rooms()
//            messages()
//        }
//    } else {
//        Row(modifier = Modifier.fillMaxSize()) {
//            Surface(modifier = Modifier.fillMaxHeight().width(200.dp), tonalElevation = 4.dp) {
//                rooms()
//            }
//            VerticalDivider()
//            messages()
//        }
//    }
//}