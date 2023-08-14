package com.bedesv.budgettracker

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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
            text = stringResource(id = R.string.transactions_list_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(navigationController: NavController, inputTransactions: List<Transaction>? = null) {
    val transactionService = TransactionService.getInstance()

    val transactions = if (inputTransactions.isNullOrEmpty()) {
        remember{ mutableStateOf(transactionService.getAll())}
    } else {
        remember{ mutableStateOf(inputTransactions)}
    }


    Log.d("Transaction List", transactionService.getAll().toString())
    LazyColumn(modifier = Modifier.fillMaxHeight(0.8f)
        ) {
        stickyHeader { TransactionHeader() }
        items(transactions.value) { transaction ->
            TransactionItem(navigationController, transaction, transactions.value) { deletedTransactionIndex ->
                val updatedTransactions = transactions.value.toMutableList()
                updatedTransactions.removeAt(deletedTransactionIndex)
                transactions.value = updatedTransactions
            }
        }
    }
}

@Composable
fun TransactionItem(navigationController: NavController,
                    transaction: Transaction,
                    transactions: List<Transaction>,
                    onDelete: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val transactionService = TransactionService.getInstance()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        expanded = true
                    }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (Modifier.weight(0.4f)) {
            Text(
                text = transaction.getDate(),
                style = MaterialTheme.typography.bodyMedium
            )
            val transactionStringFormat: String = if (transaction.expense) {
                "-\$%.2f"
            } else {
                "\$%.2f"
            }
            Text(
                text = String.format(transactionStringFormat,transaction.amount),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column (Modifier.weight(0.65f)){
            Text(
                text = transaction.notes,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(Modifier.weight(0.1f)) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {  Text(stringResource(id = R.string.edit)) },
                    onClick = {
                        navigationController.navigate(Screen.AddTransactionScreen.route + "/${transaction.uid}")
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.delete)) },
                    onClick = {
                        transactionService.deleteByUid(transaction.uid)
                        onDelete(transactions.indexOf(transaction))
                        expanded = false
                    }
                )
            }
        }
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

