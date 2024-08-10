package io.ktor.chat

import kotlin.test.Test

class ListRepositoryTest {
    
    val repository = ListRepository.create(
        User(1L, "Leonardo"),
        User(2L, "Donatello"),
        User(3L, "Michelangelo"),
        User(4L, "Raphael"),
    ) { user, id ->
        user.copy(id = id)
    }
    
//    @Test
//    fun querying() = runTest {
//        repository.list().size
//    }
    
}