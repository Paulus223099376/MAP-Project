package com.valentinesgarage.app.data.repository

import com.valentinesgarage.app.data.local.dao.TruckDao
import com.valentinesgarage.app.data.local.entity.TruckEntity
import com.valentinesgarage.app.domain.model.Truck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Read/write access to truck records. Exposes domain models so the
 * presentation layer never sees Room entities.
 */
class TruckRepository(private val dao: TruckDao) {

    fun observeAll(): Flow<List<Truck>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun search(query: String): Flow<List<Truck>> =
        dao.search(query.trim()).map { rows -> rows.map { it.toDomain() } }

    fun observeRecent(limit: Int): Flow<List<Truck>> =
        dao.observeRecent(limit).map { rows -> rows.map { it.toDomain() } }

    fun observeById(id: Long): Flow<Truck?> =
        dao.observeById(id).map { it?.toDomain() }

    fun observeCount(): Flow<Int> = dao.observeCount()
    fun observeInProgressCount(): Flow<Int> = dao.observeInProgressCount()
    fun observeCompletedCount(): Flow<Int> = dao.observeCompletedCount()
    fun observeCheckInsSince(sinceMillis: Long): Flow<Int> = dao.observeCheckInsSince(sinceMillis)
    fun observeCheckInsByEmployee(employeeId: Long): Flow<Int> =
        dao.observeCheckInsByEmployee(employeeId)

    suspend fun findById(id: Long): Truck? = dao.findById(id)?.toDomain()

    suspend fun checkIn(truck: Truck): Long =
        dao.insert(TruckEntity.fromDomain(truck.copy(id = 0)))

    suspend fun update(truck: Truck) =
        dao.update(TruckEntity.fromDomain(truck))

    suspend fun setCompleted(truckId: Long, isCompleted: Boolean, completedAt: Long?) =
        dao.setCompleted(truckId, isCompleted, completedAt)
}
