package com.bedesv.budgettracker

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun HomeScreen(navigationController: NavController) {
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
            }
        } else -> {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)
                                      .fillMaxHeight()) {
                TransactionList(navigationController)
            }
            Column(
                modifier = Modifier.fillMaxWidth(1f)
                                      .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { navigationController.navigate(Screen.AddTransactionScreen.route + "/-1") }) {
                    Text(text = stringResource(id = R.string.add_transaction_button))
                }
            }


        }

        }
    }
}

