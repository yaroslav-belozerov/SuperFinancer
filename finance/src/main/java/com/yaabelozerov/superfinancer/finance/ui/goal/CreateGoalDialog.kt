package com.yaabelozerov.superfinancer.finance.ui.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButton
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.finance.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateGoalDialog(onHide: () -> Unit, onCreate: (String, Long, String, Long?) -> Unit) {
    val scope = rememberCoroutineScope()
    var imagePath by remember { mutableStateOf<String?>(null) }
    var dateEpochMillis by remember { mutableStateOf<Long?>(null) }
    var openDatePicker by remember { mutableStateOf(false) }

    CardDialog(stringResource(R.string.create_a_goal), onDismiss = {
        scope.launch {
            onHide()
            imagePath?.let {
                CommonModule.mediaManager.removeMedia(it)
            }
        }
    }) {
        imagePath?.let {
            PhotoPickerImage(it) { imagePath = null }
        } ?: PhotoPickerButton { imagePath = it }
        var name by remember { mutableStateOf("") }
        OutlinedTextField(name,
            onValueChange = { name = it },
            placeholder = { Text(stringResource(R.string.name_your_goal)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        )
        var amount by remember { mutableLongStateOf(0L) }
        OutlinedTextField(
            amount.toString(),
            { amount = it.toLongOrNull() ?: 0L },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            trailingIcon = {
                Icon(
                    Icons.Default.CurrencyRuble,
                    contentDescription = null,
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )
        dateEpochMillis?.let {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).format(withTime = false))
                IconButton(onClick = { dateEpochMillis = null }) { Icon(Icons.Default.Close, contentDescription = null) }
            }
        } ?: TextButton(onClick = { openDatePicker = true }) {
            Text(stringResource(R.string.add_deadline))
        }
        val saveEnabled by remember(name, amount) {
            mutableStateOf(name.isNotBlank() && amount > 0)
        }
        Button(
            onClick = {
                if (amount > 0 && name.isNotBlank()) {
                    onCreate(name, amount, imagePath ?: "", dateEpochMillis)
                    onHide()
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = saveEnabled
        ) { Text(stringResource(R.string.save)) }
        if (openDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(onDismissRequest = { openDatePicker = false }, confirmButton = {
                Button(onClick = {
                    dateEpochMillis = datePickerState.selectedDateMillis
                    openDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            }) {
                DatePicker(datePickerState)
            }
        }
    }
}
