package com.bedesv.budgettracker

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TransactionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Transactions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(transactions: List<Transaction>) {
    LazyColumn(modifier = Modifier.fillMaxHeight(0.8f)
        ) {
        stickyHeader { TransactionHeader() }
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures (
                    onLongPress = {
                        TODO("Make edit and delete options show at bottom of the screen")
                        Toast
                            .makeText(context, "Delete", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = transaction.getDate(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("$%.2f",transaction.amount),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = transaction.notes,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

