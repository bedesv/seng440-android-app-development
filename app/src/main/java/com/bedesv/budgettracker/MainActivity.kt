package com.bedesv.budgettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.AddTransactionScreen.route + "/{transactionUid}") {
            val arguments = requireNotNull(it.arguments)
            val transactionUid = requireNotNull(arguments.getString("transactionUid"))
            AddEditTransactionScreen(navController, transactionUid.toInt())
        }
        composable(route = Screen.WeeklyStatsScreen.route) {
            WeeklyStatsScreen(navController)
        }
    }
}