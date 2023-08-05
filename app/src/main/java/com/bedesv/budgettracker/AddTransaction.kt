package com.bedesv.budgettracker
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.bedesv.budgettracker.ui.theme.BudgetTrackerTheme
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Calendar

class AddTransaction : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransactionForm(amountValue = "0")
                }
            }
        }
    }
}

fun isValidTransactionAmount(transactionAmountStr: String): Boolean {
    println(transactionAmountStr)

    if (transactionAmountStr.isBlank()) {
        return false
    }


    if (Regex("0\\.([0-9]{1,2})").matches(transactionAmountStr)) { // From 0.X or 0.XX
        if ((transactionAmountStr.toBigDecimal().compareTo(BigDecimal.ZERO) != 0)) {
            return true
        }
        return false
    } else if (Regex("[1-9][0-9]*\\.[0-9]{1,2}").matches(transactionAmountStr) or // Form X.X or X.XX
               Regex("[1-9][0-9]*").matches(transactionAmountStr)) { // Form XX
        return true
    }

    return false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionForm(amountValue: String) {
    var transactionAmount by remember { mutableStateOf(TextFieldValue(amountValue))}
    var transactionAmountError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    // Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    var transactionDateText by remember { mutableStateOf("$dayOfMonth/${month + 1}/$year")}


    val datePicker = DatePickerDialog(context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            transactionDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, dayOfMonth)


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = transactionAmount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                transactionAmount = it
                transactionAmountError = !isValidTransactionAmount(it.text)
            },
            isError = transactionAmountError,
            label = { Text(text = "Transaction Amount ($)") },
            placeholder = { Text(text = "Enter the transaction amount in dollars") }
        )
        val calendarIcon = @Composable {IconButton(onClick = { datePicker.show() }) {
            Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "Select transaction date")
        }} //TODO: Make date save when tap off date picker

        TextField(value = transactionDateText,
            onValueChange = {transactionDateText = it},
            trailingIcon = calendarIcon,
            readOnly = true)

    }

}

@Preview(showBackground = true)
@Composable
fun TransactionFormPreview() {
    BudgetTrackerTheme {
        TransactionForm(amountValue = "")
    }
}