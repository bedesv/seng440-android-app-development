package com.bedesv.budgettracker
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import java.math.BigDecimal
import java.util.Calendar
import androidx.navigation.NavController

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
fun AddTransactionScreen(navigationController: NavController) {
    var transactionAmount by remember { mutableStateOf(TextFieldValue(""))}
    var transactionAmountError by remember { mutableStateOf(false) }
    var transactionNote by remember { mutableStateOf(TextFieldValue(""))}
    var transactionNoteError by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val transactionService = TransactionService.getInstance()
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
            onValueChange = {
                transactionNote = it
                transactionNoteError = transactionNote.text.isEmpty()
            },
            label = { Text(text = "Transaction Note") },
            isError = transactionNoteError)


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

        Button(onClick = {
            transactionAmountError = !isValidTransactionAmount(transactionAmount.text)
            transactionNoteError = transactionNote.text.isEmpty()
            if (transactionAmountError) {
                Toast.makeText(context, "Error: Your transaction amount is invalid", Toast.LENGTH_SHORT).show()
            } else if (transactionNoteError) {
                Toast.makeText(context, "Error: You must have a transaction note", Toast.LENGTH_SHORT).show()
            } else {
                transactionService.saveTransaction(transactionNote.text, transactionDateText, transactionAmount.text)
                navigationController.navigate(Screen.HomeScreen.route)
            }

        }) {
            Text(text = "Save Transaction")
        }
    }
}