package ktor.chat.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ktor.chat.ChatViewModel
import ktor.chat.components.ErrorText
import java.awt.TextField

@Composable
fun LoginScreen(vm: ChatViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var server by remember { mutableStateOf("http://localhost:8080") }
    var serverAvailable by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var tabIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(server) {
        // TODO debounce
        serverAvailable = vm.isServerAvailable(server)
    }
    
    @Composable
    fun ifRegistering(block: @Composable () -> Unit) =
        if (tabIndex == 1) block() else {}

    fun login() {
        coroutineScope.launch {
            loading = true
            try {
                vm.login(server, email, password)
            } catch (e: Exception) {
                e.printStackTrace()
                error = e.message
            }
            finally {
                loading = false
            }
        }
    }
    
    fun register() {
        coroutineScope.launch {
            loading = true
            try {
                if (password != passwordRepeat)
                    error = "Passwords do not match"
                else
                    vm.register(server, email, name, password)
            } catch (e: Exception) {
                e.printStackTrace()
                error = e.message
            }
            finally {
                loading = false
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(30.dp))
        Text(
            "KTOR CHAT",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(30.dp))
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(text = { Text("Login") }, selected = tabIndex == 0, onClick = { tabIndex = 0 })
                Tab(text = { Text("Register") }, selected = tabIndex == 1, onClick = { tabIndex = 1 })
            }
            Column(Modifier.fillMaxWidth().height(225.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                
                TextField(
                    server,
                    { server = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Server")
                    }
                )
                TextField(
                    email,
                    { email = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Email")
                    }
                )
                ifRegistering {
                    TextField(
                        name,
                        { name = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Name")
                        }
                    )
                }
                TextField(
                    password,
                    { password = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    label = {
                        Text("Password")
                    }
                )
                ifRegistering {
                    TextField(
                        passwordRepeat,
                        { passwordRepeat = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        label = {
                            Text("Repeat password")
                        }
                    )
                }            
            }
            
            error?.let {
                ErrorText(it, modifier = Modifier.align(Alignment.End))
            }
            
            when(tabIndex) {
                0 -> Button(
                    ::login,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !loading && sequenceOf(server, email, password).all { it.isNotBlank() }
                ) {
                    Text(if (loading) "Submitting..." else "Login")
                }
                1 -> Button(
                    ::register,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !loading && sequenceOf(server, email, name, password, passwordRepeat).all { it.isNotBlank() }
                ) {
                    Text(if (loading) "Submitting..." else "Register")
                }
            }
        }
    }
}