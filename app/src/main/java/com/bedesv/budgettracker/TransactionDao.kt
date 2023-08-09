package com.bedesv.budgettracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM `Transaction` WHERE date BETWEEN :startDate AND :endDate")
    fun getInDateRange(startDate: Long, endDate: Long): List<Transaction>

    @Insert
    fun insertAll(vararg transactions: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}