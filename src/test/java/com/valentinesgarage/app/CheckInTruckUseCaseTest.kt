package com.valentinesgarage.app

import com.valentinesgarage.app.data.repository.TruckRepository
import com.valentinesgarage.app.domain.model.Truck
import com.valentinesgarage.app.domain.model.VehicleCondition
import com.valentinesgarage.app.domain.usecase.CheckInTruckUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CheckInTruckUseCase]. Validation lives in pure
 * domain code so we can test it without Android, Room, or coroutines
 * machinery.
 */
class CheckInTruckUseCaseTest {

    private val repo = mockk<TruckRepository>()
    private val useCase = CheckInTruckUseCase(repo)

    private fun input(
        plate: String = "N123ABC",
        make: String = "Volvo",
        model: String = "FH16",
        customer: String = "Acme Logistics",
        km: Int = 145_000,
        condition: VehicleCondition = VehicleCondition.GOOD,
        notes: String = "Small dent on rear left",
        empId: Long = 1L,
    ) = CheckInTruckUseCase.Input(plate, make, model, customer, km, condition, notes, empId)

    @Test
    fun `valid input is persisted with normalised plate and returns id`() = runTest {
        val saved = slot<Truck>()
        coEvery { repo.checkIn(capture(saved)) } returns 42L

        val result = useCase(input(plate = " na 123 abc ".trim()), nowMillis = 1_700_000_000L)

        assertTrue(result is CheckInTruckUseCase.Result.Success)
        assertEquals(42L, (result as CheckInTruckUseCase.Result.Success).truckId)
        assertEquals("NA 123 ABC", saved.captured.plateNumber)
        assertEquals(1_700_000_000L, saved.captured.checkedInAt)
        coVerify(exactly = 1) { repo.checkIn(any()) }
    }

    @Test
    fun `blank plate is rejected`() = runTest {
        val result = useCase(input(plate = " "), nowMillis = 0L)
        assertTrue(result is CheckInTruckUseCase.Result.Failure)
    }

    @Test
    fun `negative odometer is rejected`() = runTest {
        val result = useCase(input(km = -10), nowMillis = 0L)
        assertTrue(result is CheckInTruckUseCase.Result.Failure)
    }

    @Test
    fun `unauthenticated employee is rejected`() = runTest {
        val result = useCase(input(empId = 0L), nowMillis = 0L)
        assertTrue(result is CheckInTruckUseCase.Result.Failure)
    }
}
