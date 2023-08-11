package com.bedesv.budgettracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM TransactionDatabaseObject")
    fun getAll(): List<TransactionDatabaseObject>

    @Query("SELECT * FROM TransactionDatabaseObject WHERE date BETWEEN :startDate AND :endDate")
    fun getInDateRange(startDate: Long, endDate: Long): List<TransactionDatabaseObject>

    @Insert
    fun insertAll(vararg transactions: TransactionDatabaseObject)

    @Delete
    fun delete(transaction: TransactionDatabaseObject)
}