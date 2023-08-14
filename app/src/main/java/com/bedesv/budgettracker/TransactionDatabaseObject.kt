package com.bedesv.budgettracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionDatabaseObject(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "notes") val notes: String,
    @ColumnInfo(name = "expense") val expense: Boolean
)