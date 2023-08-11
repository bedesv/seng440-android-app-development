package com.bedesv.budgettracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(navigationController: NavController) {
    val transactionService = TransactionService.getInstance()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val transactions = transactionService.getAll()
        TransactionList(transactions)
        Button(onClick = { navigationController.navigate(Screen.AddTransactionScreen.route) }) {
            Text(text = "Add Transaction")
        }
    }


}

