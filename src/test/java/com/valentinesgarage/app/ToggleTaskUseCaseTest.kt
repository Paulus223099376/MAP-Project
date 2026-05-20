package com.valentinesgarage.app

import com.valentinesgarage.app.data.repository.RepairTaskRepository
import com.valentinesgarage.app.domain.usecase.ToggleTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class ToggleTaskUseCaseTest {

    private val repo = mockk<RepairTaskRepository>(relaxed = true)
    private val useCase = ToggleTaskUseCase(repo)

    @Test
    fun `toggling delegates to repository with employee context`() = runTest {
        coEvery { repo.toggleTaskDone(any(), any(), any()) } returns Unit
        useCase(taskId = 7L, employeeId = 3L, nowMillis = 99L)
        coVerify(exactly = 1) { repo.toggleTaskDone(7L, 3L, 99L) }
    }

    @Test
    fun `toggling without an employee throws`() = runTest {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(taskId = 1L, employeeId = 0L, nowMillis = 0L)
            }
        }
    }
}
