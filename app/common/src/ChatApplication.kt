package ktor.chat

import androidx.compose.runtime.*
import androidx.compose.ui.layout.Layout
import ktor.chat.login.ConfirmationScreen
import ktor.chat.login.LoginScreen
import ktor.chat.vm.ChatViewModel
import ktor.chat.vm.Confirmation
import ktor.chat.vm.createViewModel

@Composable
fun ChatApplication(vm: ChatViewModel = createViewModel()) {
    val loggedInUser by vm.loggedInUser
    val confirmation by vm.confirmation
    var screenSize by remember { vm.screenSize }

    Layout(
        content = {
            if (confirmation is Confirmation.Pending)
                ConfirmationScreen(vm)
            else if (loggedInUser == null)
                LoginScreen(vm)
            else
                ChatScreen(vm)
        },
        // Complicated bit of code to get the window dimensions
        measurePolicy = { measurables, constraints ->
            // Use the max width and height from the constraints
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            screenSize = Pair(width, height)

            // Measure and place children composables
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            layout(width, height) {
                var yPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = yPosition)
                    yPosition += placeable.height
                }
            }
        }
    )
}
