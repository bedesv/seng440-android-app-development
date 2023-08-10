package com.bedesv.budgettracker

import androidx.room.Delete
import androidx.room.Insert
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.github.oshai.kotlinlogging.KotlinLogging


class TransactionService {

    private val logger = KotlinLogging.logger{}
    private val db = AppDatabase.getInstance()
    private val transactionDao = db.transactionDao()

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    companion object {
        private var instance: TransactionService? = null

        @Synchronized
        fun getInstance(): TransactionService {
            if (instance == null) {
                instance = TransactionService()
            }
            return instance!!
        }
    }

    fun saveTransaction(notes: String, date: String, amount: String) {
        val transaction = Transaction(amount=amount.toFloat(), date=LocalDate.parse(date, dateTimeFormatter).toEpochDay(), notes=notes)

        transactionDao.insertAll(transaction)

        logger.info { "Added transaction $transaction to database" }
    }

    fun getAll(): List<Transaction> {
        return transactionDao.getAll()
    }


    fun insertAll(vararg transactions: Transaction) {
        transactionDao.insertAll(*transactions)
    }


    fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }


}