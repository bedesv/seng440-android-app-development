package com.bedesv.budgettracker
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import androidx.navigation.NavController
import java.time.LocalDate

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
fun AddEditTransactionScreen(navigationController: NavController,
                             transactionUid: Int = -1) {

    val transactionService = TransactionService.getInstance()

    val editing: Boolean
    val transactionAmountInitial: String
    val transactionNotesInitial: String
    val transactionDateInitial: Long

    if (transactionUid != -1) {
        val transaction: Transaction = transactionService.getByUid(transactionUid)

        editing = true
        transactionAmountInitial = transaction.amount.toString()
        transactionNotesInitial = transaction.notes
        transactionDateInitial = transaction.date
    } else {
        editing = false
        transactionAmountInitial = ""
        transactionNotesInitial = ""
        transactionDateInitial = LocalDate.now().toEpochDay()
    }

    var transactionAmount by remember { mutableStateOf(TextFieldValue(transactionAmountInitial)) }
    var transactionAmountError by remember { mutableStateOf(false) }
    var transactionNote by remember { mutableStateOf(TextFieldValue(transactionNotesInitial)) }
    var transactionNoteError by remember { mutableStateOf(false) }
    var transactionIsExpense by remember {mutableStateOf(true)}


    val context = LocalContext.current

    val date = LocalDate.ofEpochDay(transactionDateInitial)
    val year = date.year
    val month = date.monthValue - 1
    val dayOfMonth = date.dayOfMonth

    var transactionDateText by remember { mutableStateOf("$dayOfMonth/${month + 1}/$year")}

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            transactionDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear" },
        year, month, dayOfMonth
    )
    datePicker.setCanceledOnTouchOutside(false)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text=stringResource(id = R.string.expense),
                style = MaterialTheme.typography.titleLarge
            )
            Checkbox(
                checked = transactionIsExpense,
                onCheckedChange = {isChecked: Boolean ->
                    transactionIsExpense = isChecked
                }
            )
        }
        TextField(
            value = transactionNote,
            onValueChange = {
                transactionNote = it
                transactionNoteError = transactionNote.text.isEmpty()
            },
            label = { Text(text = stringResource(id = R.string.transaction_note)) },
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
            label = { Text(text = stringResource(id = R.string.transaction_amount)) },
        )

        val calendarIcon = @Composable {Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = stringResource(id = R.string.datepicker_context))}

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
        val transactionAmountErrorMessage = stringResource(id = R.string.transaction_amount_error_message)
        val transactionNoteErrorMessage = stringResource(id = R.string.transaction_note_error_message)

        Button(onClick = {
            transactionAmountError = !isValidTransactionAmount(transactionAmount.text)
            transactionNoteError = transactionNote.text.isEmpty()
            if (transactionNoteError) {
                Toast.makeText(context, transactionNoteErrorMessage, Toast.LENGTH_SHORT).show()
            } else if (transactionAmountError) {
                Toast.makeText(context, transactionAmountErrorMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (editing) {
                    transactionService.updateTransaction(transactionUid, transactionNote.text, transactionDateText, transactionAmount.text, transactionIsExpense)
                } else {
                    transactionService.saveTransaction(transactionNote.text, transactionDateText, transactionAmount.text, transactionIsExpense)
                }
                navigationController.navigate(Screen.HomeScreen.route)
            }
        }) {
            Text(text = stringResource(id = R.string.save_transaction_button))
        }
    }
}