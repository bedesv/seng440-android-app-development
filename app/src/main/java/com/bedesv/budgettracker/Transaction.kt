package com.bedesv.budgettracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Transaction(val amount: Float, val date: Long, val notes: String, val expense: Boolean, val uid: Int = 0) {


    constructor(transactionDatabaseObject: TransactionDatabaseObject): this(transactionDatabaseObject.amount,
                                                                            transactionDatabaseObject.date,
                                                                            transactionDatabaseObject.notes,
                                                                            transactionDatabaseObject.expense,
                                                                            transactionDatabaseObject.uid)
    fun getDate(): String {
        val dateObject = LocalDate.ofEpochDay(date)
        val dayNumberSuffix: String = getDayNumberSuffix(dateObject.dayOfMonth)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("d'$dayNumberSuffix 'MMMM uuuu")
        return dateObject.format(dateTimeFormatter).toString()
    }

    private fun getDayNumberSuffix(day: Int): String {
        if (day in 11..13) {
            return "th"
        }
        return when (day % 10) {
            1 ->
                "st"
            2 ->
                "nd"
            3 ->
                "rd"
            else ->
                "th"
        }
    }
}