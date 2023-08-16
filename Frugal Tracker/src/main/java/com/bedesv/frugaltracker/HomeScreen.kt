package com.bedesv.frugaltracker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navigationController: NavController) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)) {
                    TransactionList(navigationController)
                }
                HomeScreenButtons(navigationController)
            }
        } else -> {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight()) {
                    TransactionList(navigationController)
                }
                HomeScreenButtons(navigationController)
            }
        }
    }
}

@Composable
fun HomeScreenButtons(navigationController: NavController) {
    val context = FrugalTrackerApplication.AppContextManager.getAppContext()
    val transactionService = TransactionService.getInstance()
    val saveLocation = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(CreateDocument("text/comma-separated-values")) { uri ->
        saveLocation.value = uri
    }
    val exportTransactionsErrorMessage = stringResource(R.string.export_transactions_error_message)
    val exportTransactionsSuccessMessage = stringResource(R.string.export_transactions_success_message)

    val openDialog = remember { mutableStateOf(true) }

    saveLocation.value?.let { uri ->
        uri.path?.let {
            if (transactionService.exportTransactionsToCSV(uri, transactionService.getAll())) {
                Toast.makeText(context, exportTransactionsSuccessMessage, Toast.LENGTH_SHORT).show()
                val activityContext = LocalContext.current
                openDialog.value = true
                OpenCSVDialog(openDialog, uri, activityContext)
            } else {
                Toast.makeText(context, exportTransactionsErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Button(onClick = { navigationController.navigate(Screen.AddTransactionScreen.route + "/-1") }) {
                Text(text = stringResource(id = R.string.add_transaction_button))
            }
        }
        Row {
            Button(onClick = { navigationController.navigate(Screen.WeeklyStatsScreen.route) }) {
                Text(text = stringResource(id = R.string.weekly_stats_button))
            }
        }
        Row {
            Button(onClick = { launcher.launch("output.csv") }) {
                Text(text = stringResource(id = R.string.export_transactions_button))
            }
        }
    }
}

@Composable
fun OpenCSVDialog(openDialog: MutableState<Boolean>, uri: Uri, context: Context) {
    val coroutineScope = rememberCoroutineScope()
    val dialogTitle = stringResource(id = R.string.intent_title)
    val errorToast = stringResource(id = R.string.intent_error_toast)

    if (openDialog.value) {
        AlertDialog(
            title = { Text(dialogTitle) },
            text = { Text(stringResource(id = R.string.open_csv_dialog_text)) },
            onDismissRequest = {
                openDialog.value = false
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(stringResource(id = R.string.no))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    coroutineScope.launch {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(uri, "text/comma-separated-values")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        val chooser = Intent.createChooser(intent, dialogTitle)

                        try {
                            context.startActivity(chooser)
                        } catch (e: Exception) {
                            e.message?.let { Log.e("Home Screen", it) }
                            Toast.makeText(
                                context,
                                errorToast,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }) {
                    Text(stringResource(id = R.string.yes))
                }
            }
        )
    }
}
