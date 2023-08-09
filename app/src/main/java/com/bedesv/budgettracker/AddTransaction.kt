package com.bedesv.budgettracker
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.bedesv.budgettracker.ui.theme.BudgetTrackerTheme
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
                    TransactionForm(amountValue = "")
                }
            }
        }
    }
}

fun isValidTransactionAmount(transactionAmountStr: String): Boolean {
    if (transactionAmountStr.isBlank()) {
        return false
    }

    if (Regex("0\\.([0-9]{1,2})").matches(transactionAmountStr)) { // Form 0.X or 0.XX
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
    var transactionNote by remember { mutableStateOf(TextFieldValue(""))}

    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    // Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    var transactionDateText by remember { mutableStateOf("$dayOfMonth/${month + 1}/$year")}

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            transactionDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear" },
        year, month, dayOfMonth
    )
    datePicker.setCanceledOnTouchOutside(false)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = transactionNote,
            onValueChange = { transactionNote = it },
            label = { Text(text = "Transaction Note") })

        TextField(value = transactionAmount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (!(it.text.isNotEmpty() && !Regex("[0-9]|\\.").matches(it.text[it.text.length - 1].toString()))) {
                    transactionAmount = it
                }
                transactionAmountError = !isValidTransactionAmount(transactionAmount.text)
            },
            isError = transactionAmountError,
            label = { Text(text = "Transaction Amount ($)") },
        )

        val calendarIcon = @Composable {Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "Select transaction date")}

        TextField(value = transactionDateText,
            onValueChange = {transactionDateText = it},
            trailingIcon = calendarIcon,
            readOnly = true,
            modifier = Modifier.pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        datePicker.show()
                    }
                }
            }
        )
        
        Button(onClick = {saveTransaction(transactionNote.text, transactionDateText, transactionAmount.text)}) {
            Text(text = "Save Transaction")
        }
    }
}

fun saveTransaction(notes: String, date: String, amount: String) {
    val db = AppDatabase.getInstance()
    val transactionDao = db.transactionDao()
    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    val transaction = Transaction(amount=amount.toFloat(), date=LocalDate.parse(date, formatter).toEpochDay(), notes=notes)

    transactionDao.insertAll(transaction)


    println(transactionDao.getAll())


}

@Preview(showBackground = true)
@Composable
fun TransactionFormPreview() {
    BudgetTrackerTheme {
        TransactionForm(amountValue = "")
    }
}