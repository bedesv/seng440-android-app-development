package com.bedesv.budgettracker

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object AddTransactionScreen: Screen("add_transaction_screen")
    object WeeklyStatsScreen: Screen("weekly_stats_screen")
}
