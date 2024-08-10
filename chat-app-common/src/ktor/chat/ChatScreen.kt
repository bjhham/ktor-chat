package ktor.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.chat.*
import kotlinx.datetime.Clock
import ktor.chat.client.remoteList
import ktor.chat.client.remoteListWithUpdates
import ktor.chat.components.RemoteLoader
import ktor.chat.messages.MessageInput
import ktor.chat.messages.MessageList
import ktor.chat.messages.MessageListItem
import ktor.chat.rooms.RoomMenuItem
import ktor.chat.rooms.RoomsMenu
import ktor.chat.utils.*

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val roomsRemote: Remote<List<Room>> by vm.rooms.remoteList()
    val messagesRemote: Remote<SnapshotStateList<Message>> by vm.messages.remoteListWithUpdates()
    
    ChatLayout(rooms = {
        RemoteLoader(roomsRemote) { rooms ->
            RoomsMenu {
                LazyColumn {
                    items(rooms) { room ->
                        RoomMenuItem(room)
                    }
                }
            }
        }
    }) {
        RemoteLoader(messagesRemote) { messages ->
            MessageList {
                LazyColumn {
                    items(messages) { message ->
                        MessageListItem(message)
                    }
                }
                MessageInput { messageText ->
                    vm.messages.create(
                        Message(
                            author = vm.me!!,
                            created = Clock.System.now(),
                            room = 1,
                            text = messageText,
                        )
                    )
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