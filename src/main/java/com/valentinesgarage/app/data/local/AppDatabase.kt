package com.valentinesgarage.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.valentinesgarage.app.data.local.dao.EmployeeDao
import com.valentinesgarage.app.data.local.dao.RepairTaskDao
import com.valentinesgarage.app.data.local.dao.TaskNoteDao
import com.valentinesgarage.app.data.local.dao.TruckDao
import com.valentinesgarage.app.data.local.entity.EmployeeEntity
import com.valentinesgarage.app.data.local.entity.RepairTaskEntity
import com.valentinesgarage.app.data.local.entity.TaskNoteEntity
import com.valentinesgarage.app.data.local.entity.TruckEntity

@Database(
    entities = [
        TruckEntity::class,
        EmployeeEntity::class,
        RepairTaskEntity::class,
        TaskNoteEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun truckDao(): TruckDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun repairTaskDao(): RepairTaskDao
    abstract fun taskNoteDao(): TaskNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "valentines_garage.db",
                )
                    // Schema bumped to v2 (employees gained credentials). For an
                    // academic project we accept destructive migration; demo
                    // accounts are re-seeded on the next launch.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
