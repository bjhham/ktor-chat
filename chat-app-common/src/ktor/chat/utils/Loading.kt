package ktor.chat.utils

import androidx.compose.runtime.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import ktor.chat.client.ResponseFailureException
import java.lang.Exception

sealed interface Remote<T> {
    fun <S> map(mapping: (T) -> S): Remote<S>
}

class Loading<T>: Remote<T> {
    override fun <S> map(mapping: (T) -> S): Remote<S> = Loading()
}
data class Error<T>(val message: String): Remote<T> {
    override fun <S> map(mapping: (T) -> S): Remote<S> = Error(message)
}
data class Done<T>(val value: T): Remote<T> {
    override fun <S> map(mapping: (T) -> S): Remote<S> = Done(mapping(value))
}

@Composable
inline fun <T> load(vararg keys: Any, crossinline fetch: suspend () -> T): State<Remote<T>> {
    val result = remember { mutableStateOf<Remote<T>>(Loading()) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(*keys) {
        coroutineScope.launch {
            result.value = try {
                Done(fetch())
            } catch (e: Exception) {
                Error(e.message ?: "Unknown error!")
            }
        }
    }
    
    return result
}