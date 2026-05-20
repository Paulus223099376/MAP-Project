package com.valentinesgarage.app

import com.valentinesgarage.app.data.repository.RepairTaskRepository
import com.valentinesgarage.app.domain.usecase.AddTaskNoteUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class AddTaskNoteUseCaseTest {

    private val repo = mockk<RepairTaskRepository>(relaxed = true)
    private val useCase = AddTaskNoteUseCase(repo)

    @Test
    fun `valid note is forwarded to repository`() = runTest {
        useCase(taskId = 5L, employeeId = 2L, message = "Replaced front brake pads", nowMillis = 1L)
        coVerify(exactly = 1) { repo.addNote(5L, 2L, "Replaced front brake pads", 1L) }
    }

    @Test
    fun `unauthenticated employee cannot post`() {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(taskId = 1L, employeeId = 0L, message = "Hi", nowMillis = 0L)
            }
        }
    }
}
