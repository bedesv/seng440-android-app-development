package com.bedesv.budgettracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Transaction constructor(val uid: Int = 0, val amount: Float, private val date: Long, val notes: String) {

    constructor(transactionDatabaseObject: TransactionDatabaseObject): this(transactionDatabaseObject.uid,
                                                                            transactionDatabaseObject.amount,
                                                                            transactionDatabaseObject.date,
                                                                            transactionDatabaseObject.notes)

    fun getDate(): String {
        val dateObject = LocalDate.ofEpochDay(date)
        val dayNumberSuffix: String = getDayNumberSuffix(dateObject.dayOfMonth)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("d'$dayNumberSuffix 'MMMM uuuu")
        return dateObject.format(dateTimeFormatter).toString()
    }

    private fun getDayNumberSuffix(day: Int): String {
        if (day in 11..13) {
            return "th";
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