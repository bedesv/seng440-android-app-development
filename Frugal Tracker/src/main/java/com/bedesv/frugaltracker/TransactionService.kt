package com.bedesv.frugaltracker

import android.net.Uri
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

    fun saveTransaction(notes: String, date: String, amount: String, expense: Boolean) {
        val transaction = TransactionDatabaseObject(
            amount=amount.toFloat(),
            date=LocalDate.parse(date, dateTimeFormatter).toEpochDay(),
            notes=notes,
            expense=expense)

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

    fun deleteByUid(uid: Int) {
        transactionDao.deleteByUid(uid)
    }

    fun getByUid(uid: Int): Transaction {
        return Transaction(transactionDao.getByUid(uid))
    }

    fun updateTransaction(uid: Int, notes: String, date: String, amount: String, expense: Boolean) {

        transactionDao.updateTransaction(
            uid=uid,
            notes=notes,
            amount=amount.toFloat(),
            date=LocalDate.parse(date, dateTimeFormatter).toEpochDay(),
            expense = expense
        )
    }

    fun getTransactionsWithinDateRange(startDate: LocalDate, endDate: LocalDate): List<Transaction>  {
        val transactionObjects = transactionDao.getInDateRange(startDate.toEpochDay(), endDate.toEpochDay())
        val transactions: MutableList<Transaction> = ArrayList()

        for (transactionObject: TransactionDatabaseObject in transactionObjects) {
            transactions.add(Transaction(transactionObject))
        }
        return transactions
    }

    fun calculateTotalSpent(transactions: List<Transaction>): Float {
        var total = 0f
        for (transaction in transactions) {
            if (transaction.expense) {
                total += transaction.amount
            }
        }
        return total
    }
    fun exportTransactionsToCSV(uri: Uri, transactions: List<Transaction>): Boolean {
        try {
            val contentResolver = FrugalTrackerApplication.AppContextManager.getAppContext().contentResolver
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = outputStream?.bufferedWriter()
            if (writer != null) {
                writer.write("UID, Date, Amount, Note")
                writer.newLine()
                for (transaction: Transaction in transactions) {
                    writer.write("${transaction.uid}, ${transaction.getDate()}, ${transaction.amount}, ${transaction.notes}")
                    writer.newLine()
                }
                writer.flush()
            }
            outputStream?.close()
        } catch (e: Exception) {
            return false
        }

        return true
    }
}