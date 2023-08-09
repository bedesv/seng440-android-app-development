package com.bedesv.budgettracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Date

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "notes") val notes: String)