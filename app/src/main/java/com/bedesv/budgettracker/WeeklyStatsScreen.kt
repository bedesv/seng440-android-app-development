package com.bedesv.budgettracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDateTime


@Composable
fun WeeklyStatsScreen(navigationController: NavController) {

    val transactionService = TransactionService.getInstance()

    val now = LocalDateTime.now()
    val startDate = now.minusDays(now.dayOfWeek.value.toLong() - DayOfWeek.MONDAY.value.toLong()).withHour(0).withMinute(0).withSecond(0)
    val endDate = now.plusDays(DayOfWeek.SUNDAY.value.toLong() - now.dayOfWeek.value.toLong()).withHour(23).withMinute(59).withSecond(59)

    val transactions = transactionService.getTransactionsWithinDateRange(startDate.toLocalDate(), endDate.toLocalDate())

    val totalSpent = transactionService.calculateTotalSpent(transactions)


    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = String.format(stringResource(id = R.string.spent_this_week), totalSpent))
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            TransactionList(navigationController, transactions)
        }
    }

}