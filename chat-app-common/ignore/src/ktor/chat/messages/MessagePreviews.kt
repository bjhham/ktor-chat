package ktor.chat.messages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktor.chat.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

@Preview
@Composable
fun MessageListPreview() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        MessageList {
            MessageListItem(
                Message(
                    author = FullUser("Bob Kebab", "bobby@mail.com", ""),
                    room = 1,
                    created = Clock.System.now()
                        .minus(3, DateTimeUnit.MINUTE)
                        .minus(32, DateTimeUnit.SECOND),
                    text = "Hey y'all how's it hangin'?",
                    )
            )
            MessageListItem(
                Message(
                    author = FullUser("Joey Bloggs", "joey@mail.com", ""),
                    room = 1,
                    created = Clock.System.now().minus(27, DateTimeUnit.SECOND),
                    text = "S'all good with me, how bout you?",
                    )
            )
            MessageListItem(
                Message(
                    author = FullUser("Bob Kebab", "bobby@mail.com", ""),
                    room = 1,
                    created = Clock.System.now().minus(14, DateTimeUnit.SECOND),
                    text = "Not bad, thanks!",
                    )
            )
        }
        MessageInput {}
    }
}