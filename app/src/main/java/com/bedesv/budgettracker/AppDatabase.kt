package com.bedesv.budgettracker

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionDatabaseObject::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    BudgetTrackerApplication.AppContextManager.getAppContext(),
                    AppDatabase::class.java, "database-name"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
    }
}