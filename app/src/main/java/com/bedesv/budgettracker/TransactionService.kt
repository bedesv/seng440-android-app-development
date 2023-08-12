package com.bedesv.budgettracker

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
        val transaction = TransactionDatabaseObject(amount=amount.toFloat(), date=LocalDate.parse(date, dateTimeFormatter).toEpochDay(), notes=notes)

        transactionDao.insertAll(transaction)

        logger.info { "Added transaction $transaction to database" }
    }

    fun getAll(): List<Transaction> {
        val transactionObjects = transactionDao.getAll()
        val transactions: MutableList<Transaction> = ArrayList()

        for (transactionObject: TransactionDatabaseObject in transactionObjects) {
            transactions.add(Transaction(transactionObject))
        }

        return transactions
    }


    fun insertAll(vararg transactions: TransactionDatabaseObject) {
        transactionDao.insertAll(*transactions)
    }


    fun delete(transaction: TransactionDatabaseObject) {
        transactionDao.delete(transaction)
    }

    fun deleteByUid(uid: Int) {
        transactionDao.deleteByUid(uid)
    }

    fun getByUid(uid: Int): Transaction {
        return Transaction(transactionDao.getByUid(uid))
    }

    fun updateTransaction(uid: Int, notes: String, date: String, amount: String) {

        transactionDao.updateTransaction(uid=uid, notes=notes, amount=amount.toFloat(), date=LocalDate.parse(date, dateTimeFormatter).toEpochDay())
    }


}