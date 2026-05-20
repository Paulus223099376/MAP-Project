package com.valentinesgarage.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valentinesgarage.app.data.local.entity.TruckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TruckDao {

    @Query("SELECT * FROM trucks ORDER BY isCompleted ASC, checkedInAt DESC")
    fun observeAll(): Flow<List<TruckEntity>>

    @Query(
        """
        SELECT * FROM trucks
        WHERE (:query = '' OR LOWER(plateNumber) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(make) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(model) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(customerName) LIKE '%' || LOWER(:query) || '%')
        ORDER BY isCompleted ASC, checkedInAt DESC
        """
    )
    fun search(query: String): Flow<List<TruckEntity>>

    @Query("SELECT * FROM trucks WHERE id = :id")
    fun observeById(id: Long): Flow<TruckEntity?>

    @Query("SELECT * FROM trucks WHERE id = :id")
    suspend fun findById(id: Long): TruckEntity?

    @Query("SELECT COUNT(*) FROM trucks")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM trucks WHERE isCompleted = 0")
    fun observeInProgressCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM trucks WHERE isCompleted = 1")
    fun observeCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM trucks WHERE checkedInAt >= :sinceMillis")
    fun observeCheckInsSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM trucks WHERE checkedInByEmployeeId = :employeeId")
    fun observeCheckInsByEmployee(employeeId: Long): Flow<Int>

    @Query("SELECT * FROM trucks ORDER BY checkedInAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TruckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(truck: TruckEntity): Long

    @Update
    suspend fun update(truck: TruckEntity)

    @Query("UPDATE trucks SET isCompleted = :done, completedAt = :completedAt WHERE id = :id")
    suspend fun setCompleted(id: Long, done: Boolean, completedAt: Long?)
}
