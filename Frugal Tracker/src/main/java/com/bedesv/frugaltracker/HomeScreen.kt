package com.bedesv.frugaltracker

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun HomeScreen(navigationController: NavController) {

    val context = FrugalTrackerApplication.AppContextManager.getAppContext()
    val transactionService = TransactionService.getInstance()
    val saveLocation = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(CreateDocument("text/comma-separated-values")) { uri ->
        saveLocation.value = uri
    }
    val exportTransactionsErrorMessage = stringResource(R.string.export_transactions_error_message)
    val exportTransactionsSuccessMessage = stringResource(R.string.export_transactions_success_message)

    saveLocation.value?.let { uri ->
        uri.path?.let {
            if (transactionService.exportTransactionsToCSV(uri, transactionService.getAll())) {
                Toast.makeText(context, exportTransactionsSuccessMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, exportTransactionsErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TransactionList(navigationController)
                Button(onClick = { navigationController.navigate(Screen.AddTransactionScreen.route + "/-1") }) {
                    Text(text = stringResource(id = R.string.add_transaction_button))
                }
                Button(onClick = { navigationController.navigate(Screen.WeeklyStatsScreen.route) }) {
                    Text(text = stringResource(id = R.string.weekly_stats_button))
                }
                Button(onClick = { launcher.launch("output.csv") }) {
                    Text(text = stringResource(id = R.string.export_transactions_button))
                }
            }
        } else -> {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()) {
                    TransactionList(navigationController)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { navigationController.navigate(Screen.AddTransactionScreen.route + "/-1") }) {
                        Text(text = stringResource(id = R.string.add_transaction_button))
                    }
                    Button(onClick = { navigationController.navigate(Screen.WeeklyStatsScreen.route) }) {
                        Text(text = stringResource(id = R.string.weekly_stats_button))
                    }
                    Button(onClick = { launcher.launch("output.csv") }) {
                        Text(text = stringResource(id = R.string.export_transactions_button))
                    }
                }
            }
        }
    }
}

