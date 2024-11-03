package io.ktor.chat

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListRepositoryTest {

    private val leonardo = User(1L, "Leonardo")
    private val users = ListRepository.create(
        leonardo,
        User(2L, "Donatello"),
        User(3L, "Michelangelo"),
        User(4L, "Raphael"),
    )
    private val messages = ListRepository.create(
        Message(leonardo, 1L, Clock.System.now(), "Cowabunga!"),
        Message(leonardo, 2L, Clock.System.now(), "Pizza time!"),
    )
    
    @Test
    fun querying() = runTest {
        assertEquals(4, users.list().size)
        assertEquals(1, users.list {
            it["name"] = "Raphael"
        }.size)

        assertEquals(2, messages.list().size)
        assertEquals(1, messages.list {
            it["room"] = "2"
        }.size)
    }
    
}