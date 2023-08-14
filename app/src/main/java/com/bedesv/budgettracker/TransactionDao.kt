package com.bedesv.budgettracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM TransactionDatabaseObject ORDER BY date DESC")
    fun getAll(): List<TransactionDatabaseObject>

    @Query("SELECT * FROM TransactionDatabaseObject WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getInDateRange(startDate: Long, endDate: Long): List<TransactionDatabaseObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg transactions: TransactionDatabaseObject)

    @Delete
    fun delete(transaction: TransactionDatabaseObject)

    @Query("DELETE FROM TransactionDatabaseObject WHERE uid = :uid")
    fun deleteByUid(uid: Int)

    @Query("SELECT * from TransactionDatabaseObject where uid = :uid")
    fun getByUid(uid: Int): TransactionDatabaseObject

    @Query("UPDATE TransactionDatabaseObject SET notes=:notes, date=:date, amount=:amount, expense=:expense WHERE uid = :uid")
    fun updateTransaction(uid: Int, notes: String, date: Long, amount: Float, expense: Boolean)
}