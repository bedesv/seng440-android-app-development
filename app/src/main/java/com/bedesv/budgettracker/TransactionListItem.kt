package com.bedesv.budgettracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun TransactionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Transaction Amount",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Date",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Note",
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(transactions: List<Transaction>) {
    LazyColumn {
        stickyHeader { TransactionHeader() }
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = String.format("$%.2f",transaction.amount),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = LocalDate.ofEpochDay(transaction.date).toString(),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = transaction.notes,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

