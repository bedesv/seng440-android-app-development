package com.bedesv.frugaltracker

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionDatabaseObject::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    FrugalTrackerApplication.AppContextManager.getAppContext(),
                    AppDatabase::class.java, "frugal-tracker-database"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
    }
}