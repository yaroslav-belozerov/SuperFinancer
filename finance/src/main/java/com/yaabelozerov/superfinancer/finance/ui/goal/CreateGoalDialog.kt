package com.yaabelozerov.superfinancer.finance.ui.goal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButton
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import kotlinx.coroutines.launch

@Composable
internal fun CreateGoalDialog(onHide: () -> Unit, onCreate: (String, Long, String) -> Unit) {
    val scope = rememberCoroutineScope()
    var imagePath by remember { mutableStateOf<String?>(null) }

    CardDialog("Create a goal", onDismiss = {
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
            placeholder = { Text("Name your goal") },
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
        val saveEnabled by remember(name, amount) {
            mutableStateOf(name.isNotBlank() && amount > 0)
        }
        Button(
            onClick = {
                if (amount > 0 && name.isNotBlank()) {
                    onCreate(name, amount, imagePath ?: "")
                    onHide()
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = saveEnabled
        ) { Text("Save") }
    }
}
