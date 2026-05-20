package com.valentinesgarage.app.data.local

import com.valentinesgarage.app.data.local.entity.EmployeeEntity
import com.valentinesgarage.app.data.local.entity.RepairTaskEntity
import com.valentinesgarage.app.data.local.entity.TaskNoteEntity
import com.valentinesgarage.app.data.local.entity.TruckEntity
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.domain.model.VehicleCondition
import com.valentinesgarage.app.util.PasswordHasher

/**
 * Seeds the database on first launch with a working set of demo
 * accounts and a few example trucks/tasks/notes so reviewers don't open
 * the app to an empty screen.
 *
 * Demo accounts (printed in README.md as well):
 *  - valentine / Garage123    (Owner — Valentine herself)
 *  - john      / Mechanic1    (Mechanic)
 *  - peter     / Mechanic1    (Mechanic)
 *  - mary      / Mechanic1    (Mechanic)
 */
class DatabaseSeeder(private val database: AppDatabase) {

    suspend fun seedIfNeeded(nowMillis: Long) {
        val employeeDao = database.employeeDao()
        if (employeeDao.count() > 0) return

        val owner = newEmployee(
            username = "valentine",
            name = "Valentine Hangula",
            role = EmployeeRole.OWNER,
            password = "Garage123",
            email = "valentine@valentinedelgarage.na",
            phone = "+264 81 000 0001",
            createdAt = nowMillis,
        )
        val john = newEmployee("john", "John Shikongo", EmployeeRole.MECHANIC, "Mechanic1",
            "john@valentinedelgarage.na", "+264 81 000 1010", nowMillis)
        val peter = newEmployee("peter", "Peter Iyambo", EmployeeRole.MECHANIC, "Mechanic1",
            "peter@valentinedelgarage.na", "+264 81 000 1011", nowMillis)
        val mary = newEmployee("mary", "Mary Nangolo", EmployeeRole.MECHANIC, "Mechanic1",
            "mary@valentinedelgarage.na", "+264 81 000 1012", nowMillis)

        val ownerId = employeeDao.insert(owner)
        val johnId = employeeDao.insert(john)
        val peterId = employeeDao.insert(peter)
        val maryId = employeeDao.insert(mary)

        val truckDao = database.truckDao()
        val taskDao = database.repairTaskDao()
        val noteDao = database.taskNoteDao()

        val day = 24L * 60 * 60 * 1000

        // ── Truck 1 — In progress, GOOD condition
        val t1 = truckDao.insert(
            TruckEntity(
                plateNumber = "N 12345 W",
                make = "Volvo",
                model = "FH16 750",
                customerName = "Namib Logistics",
                odometerKm = 487_120,
                condition = VehicleCondition.GOOD.name,
                conditionNotes = "Light scratch on driver door. Fuel at 1/4.",
                checkedInAt = nowMillis - 2 * day,
                checkedInByEmployeeId = johnId,
            )
        )
        val t1Task1 = taskDao.insert(RepairTaskEntity(truckId = t1, title = "Replace front brake pads",
            description = "Both sides pads worn below 3 mm.", isDone = true,
            completedByEmployeeId = peterId, completedAt = nowMillis - day))
        taskDao.insert(RepairTaskEntity(truckId = t1, title = "Engine oil and filter change",
            description = "Use 15W-40 fully synthetic.", isDone = false,
            completedByEmployeeId = null, completedAt = null))
        taskDao.insert(RepairTaskEntity(truckId = t1, title = "Inspect AdBlue system",
            description = "Customer reports warning light.", isDone = false,
            completedByEmployeeId = null, completedAt = null))
        noteDao.insert(TaskNoteEntity(taskId = t1Task1, authorEmployeeId = peterId,
            message = "Pads replaced, also greased calipers.", createdAt = nowMillis - day + 3_600_000))

        // ── Truck 2 — In progress, POOR condition
        val t2 = truckDao.insert(
            TruckEntity(
                plateNumber = "N 78321 WB",
                make = "MAN",
                model = "TGS 33.480",
                customerName = "Atlantic Freight",
                odometerKm = 921_540,
                condition = VehicleCondition.POOR.name,
                conditionNotes = "Multiple body dents on left side. Cracked windscreen. Smells of diesel inside cab.",
                checkedInAt = nowMillis - day,
                checkedInByEmployeeId = maryId,
            )
        )
        val t2Task1 = taskDao.insert(RepairTaskEntity(truckId = t2, title = "Diagnose diesel leak",
            description = "Strong smell, find source before anything else.", isDone = false,
            completedByEmployeeId = null, completedAt = null))
        taskDao.insert(RepairTaskEntity(truckId = t2, title = "Replace windscreen",
            description = "Quote already approved by customer.", isDone = false,
            completedByEmployeeId = null, completedAt = null))
        taskDao.insert(RepairTaskEntity(truckId = t2, title = "Bodywork left rear panel",
            description = "Cosmetic, schedule after mechanical work.", isDone = false,
            completedByEmployeeId = null, completedAt = null))
        noteDao.insert(TaskNoteEntity(taskId = t2Task1, authorEmployeeId = maryId,
            message = "Suspect injector seals, ordered new set.", createdAt = nowMillis - 3_600_000))

        // ── Truck 3 — Completed, EXCELLENT
        val t3 = truckDao.insert(
            TruckEntity(
                plateNumber = "N 4456 WB",
                make = "Scania",
                model = "R450",
                customerName = "Erongo Mining",
                odometerKm = 215_980,
                condition = VehicleCondition.EXCELLENT.name,
                conditionNotes = "Routine 200,000 km service. Vehicle is clean and well maintained.",
                checkedInAt = nowMillis - 5 * day,
                checkedInByEmployeeId = johnId,
                isCompleted = true,
                completedAt = nowMillis - 3 * day,
            )
        )
        val t3Task1 = taskDao.insert(RepairTaskEntity(truckId = t3, title = "Full service per Scania schedule",
            description = "Oils, filters, belts checked.", isDone = true,
            completedByEmployeeId = johnId, completedAt = nowMillis - 4 * day))
        val t3Task2 = taskDao.insert(RepairTaskEntity(truckId = t3, title = "Brake pad rotation",
            description = "Front-rear swap as per maintenance plan.", isDone = true,
            completedByEmployeeId = peterId, completedAt = nowMillis - 3 * day - 7_200_000))
        noteDao.insert(TaskNoteEntity(taskId = t3Task1, authorEmployeeId = johnId,
            message = "All filters changed, tightened to spec.", createdAt = nowMillis - 4 * day + 1_800_000))
        noteDao.insert(TaskNoteEntity(taskId = t3Task2, authorEmployeeId = peterId,
            message = "Brakes look great — barely worn.", createdAt = nowMillis - 3 * day - 5_400_000))

        // Suppress unused warning on the owner id — present so the
        // owner can be looked up even before they sign in.
        @Suppress("UnusedVariable") val _o = ownerId
    }

    private fun newEmployee(
        username: String,
        name: String,
        role: EmployeeRole,
        password: String,
        email: String,
        phone: String,
        createdAt: Long,
    ): EmployeeEntity {
        val salt = PasswordHasher.newSalt()
        return EmployeeEntity(
            username = username,
            name = name,
            role = role.name,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            email = email,
            phone = phone,
            createdAt = createdAt,
            isActive = true,
        )
    }
}
