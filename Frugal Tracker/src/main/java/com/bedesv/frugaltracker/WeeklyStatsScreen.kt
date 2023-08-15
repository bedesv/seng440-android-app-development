package com.bedesv.frugaltracker

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime


@Composable
fun WeeklyStatsScreen(navigationController: NavController) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            PortraitStatsView(navigationController)
        } else -> {
            LandscapeStatsView(navigationController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeStatsView(navigationController: NavController) {

    val context = FrugalTrackerApplication.AppContextManager.getAppContext()

    val userPreferencesStore = UserPreferencesStore(context)
    val coroutineScope = rememberCoroutineScope()
    val transactionService = TransactionService.getInstance()

    val now = LocalDateTime.now()
    val startDate = now.minusDays(now.dayOfWeek.value.toLong() - DayOfWeek.MONDAY.value.toLong()).withHour(0).withMinute(0).withSecond(0)
    val endDate = now.plusDays(DayOfWeek.SUNDAY.value.toLong() - now.dayOfWeek.value.toLong()).withHour(23).withMinute(59).withSecond(59)

    val transactions = remember { mutableStateOf(transactionService.getTransactionsWithinDateRange(startDate.toLocalDate(), endDate.toLocalDate())) }

    var totalSpent by remember { mutableFloatStateOf(transactionService.calculateTotalSpent(transactions.value)) }
    val userBudgetPreference = userPreferencesStore.getUserBudget.collectAsState(initial = "")
    var currentBudget by remember { mutableStateOf(userBudgetPreference.value) }
    var budgetProgress by remember { mutableFloatStateOf(0f) }
    val textStyleTitleLarge = MaterialTheme.typography.titleLarge

    LaunchedEffect(userBudgetPreference.value) {
        currentBudget = userBudgetPreference.value
    }

    LaunchedEffect(transactions.value) {
        totalSpent = transactionService.calculateTotalSpent(transactions.value)
    }

    LaunchedEffect(totalSpent, currentBudget) {
        val budgetFloat = if (currentBudget.isNotEmpty()) currentBudget.toFloat() else 1f
        budgetProgress = (if (budgetFloat != 0f) totalSpent / budgetFloat else 0f).coerceIn(0f,1f)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start
        ) {
            TransactionList(navigationController, transactions)
        }
        Column(modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    var textStyleCurrentBudget by remember { mutableStateOf(textStyleTitleLarge)}
                    var readyToDrawCurrentBudget by remember { mutableStateOf(false) }
                    Text(
                        text = stringResource(id = R.string.weekly_budget),
                        style = textStyleCurrentBudget,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.drawWithContent {
                            if (readyToDrawCurrentBudget) drawContent()
                        },
                        onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.didOverflowWidth) {
                                textStyleCurrentBudget = textStyleCurrentBudget.copy(fontSize = textStyleCurrentBudget.fontSize * 0.99)
                            } else {
                                println(textStyleCurrentBudget.fontSize)
                                readyToDrawCurrentBudget = true
                            }
                        }
                    )
                }

                var currentBudgetError by remember { mutableStateOf(false) }


                Column(modifier = Modifier
                    .weight(0.2f)) {
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = currentBudget,
                        onValueChange = { newBudget ->
                            if (newBudget.matches(Regex("[1-9][0-9]*")) or newBudget.matches(Regex(""))) {
                                currentBudget = newBudget
                                currentBudgetError = newBudget.matches(Regex(""))
                                coroutineScope.launch {
                                    userPreferencesStore.saveBudget(newBudget)
                                }
                            }
                        },
                        isError = currentBudgetError,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                var textStyleSpentThisWeek by remember { mutableStateOf(textStyleTitleLarge)}
                var readyToDrawSpentThisWeek by remember { mutableStateOf(false) }
                Text(
                    text = String.format(stringResource(id = R.string.spent_this_week), totalSpent, now.dayOfWeek.value),
                    style = textStyleSpentThisWeek,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.drawWithContent {
                        if (readyToDrawSpentThisWeek) drawContent()
                    },
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.didOverflowWidth) {
                            textStyleSpentThisWeek = textStyleSpentThisWeek.copy(fontSize = textStyleSpentThisWeek.fontSize * 0.99)
                        } else {
                            println(textStyleSpentThisWeek.fontSize)
                            readyToDrawSpentThisWeek = true
                        }
                    }
                )

            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedLinearProgressIndicator(budgetProgress, 1500)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitStatsView(navigationController: NavController) {

    val context = FrugalTrackerApplication.AppContextManager.getAppContext()

    val userPreferencesStore = UserPreferencesStore(context)
    val coroutineScope = rememberCoroutineScope()
    val transactionService = TransactionService.getInstance()

    val now = LocalDateTime.now()
    val startDate = now.minusDays(now.dayOfWeek.value.toLong() - DayOfWeek.MONDAY.value.toLong()).withHour(0).withMinute(0).withSecond(0)
    val endDate = now.plusDays(DayOfWeek.SUNDAY.value.toLong() - now.dayOfWeek.value.toLong()).withHour(23).withMinute(59).withSecond(59)

    val transactions = remember { mutableStateOf(transactionService.getTransactionsWithinDateRange(startDate.toLocalDate(), endDate.toLocalDate())) }

    var totalSpent by remember { mutableFloatStateOf(transactionService.calculateTotalSpent(transactions.value)) }
    val userBudgetPreference = userPreferencesStore.getUserBudget.collectAsState(initial = "")
    var currentBudget by remember { mutableStateOf(userBudgetPreference.value) }
    var budgetProgress by remember { mutableFloatStateOf(0f) }
    val textStyleTitleLarge = MaterialTheme.typography.titleLarge

    LaunchedEffect(userBudgetPreference.value) {
        currentBudget = userBudgetPreference.value
    }

    LaunchedEffect(transactions.value) {
        totalSpent = transactionService.calculateTotalSpent(transactions.value)
    }

    LaunchedEffect(totalSpent, currentBudget) {
        val budgetFloat = if (currentBudget.isNotEmpty()) currentBudget.toFloat() else 1f
        budgetProgress = (if (budgetFloat != 0f) totalSpent / budgetFloat else 0f).coerceIn(0f,1f)
    }
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                var textStyleCurrentBudget by remember { mutableStateOf(textStyleTitleLarge)}
                var readyToDrawCurrentBudget by remember { mutableStateOf(false) }
                Text(
                    text = stringResource(id = R.string.weekly_budget),
                    style = textStyleCurrentBudget,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.drawWithContent {
                        if (readyToDrawCurrentBudget) drawContent()
                    },
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.didOverflowWidth) {
                            textStyleCurrentBudget = textStyleCurrentBudget.copy(fontSize = textStyleCurrentBudget.fontSize * 0.99)
                        } else {
                            println(textStyleCurrentBudget.fontSize)
                            readyToDrawCurrentBudget = true
                        }
                    }
                )
            }

            var currentBudgetError by remember { mutableStateOf(false) }


            Column(modifier = Modifier
                .weight(0.2f)) {
                TextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = currentBudget,
                    onValueChange = { newBudget ->
                        if (newBudget.matches(Regex("[1-9][0-9]*")) or newBudget.matches(Regex(""))) {
                            currentBudget = newBudget
                            currentBudgetError = newBudget.matches(Regex(""))
                            coroutineScope.launch {
                                userPreferencesStore.saveBudget(newBudget)
                            }
                        }
                    },
                    isError = currentBudgetError,
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(0.3f)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            var textStyleSpentThisWeek by remember { mutableStateOf(textStyleTitleLarge)}
            var readyToDrawSpentThisWeek by remember { mutableStateOf(false) }
            Text(
                text = String.format(stringResource(id = R.string.spent_this_week), totalSpent, now.dayOfWeek.value),
                style = textStyleSpentThisWeek,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.drawWithContent {
                    if (readyToDrawSpentThisWeek) drawContent()
                },
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        textStyleSpentThisWeek = textStyleSpentThisWeek.copy(fontSize = textStyleSpentThisWeek.fontSize * 0.99)
                    } else {
                        println(textStyleSpentThisWeek.fontSize)
                        readyToDrawSpentThisWeek = true
                    }
                }
            )

        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedLinearProgressIndicator(budgetProgress, 1500)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedLinearProgressIndicator(budgetProgress, 1500)
        }
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.Bottom
        ) {
            TransactionList(navigationController, transactions)
        }
    }
}

@Composable
fun AnimatedLinearProgressIndicator(indicatorProgress: Float, progressAnimDurationMilliseconds: Int) {
    var progress by remember { mutableFloatStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = indicatorProgress,
        animationSpec = tween(durationMillis = progressAnimDurationMilliseconds, easing = FastOutSlowInEasing)
    )
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .height(32.dp),
        progress = progressAnimation
    )
    LaunchedEffect(indicatorProgress) {
        progress = indicatorProgress
    }
}