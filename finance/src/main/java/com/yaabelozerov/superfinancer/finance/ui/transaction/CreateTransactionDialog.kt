package com.yaabelozerov.superfinancer.finance.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.horizontalFadingEdge
import com.yaabelozerov.superfinancer.finance.domain.Goal

@Composable
internal fun CreateTransactionDialog(
    onHide: () -> Unit,
    goals: List<Goal>,
    onCreate: (Long, Long, String) -> Unit,
    chosen: Pair<Long, (Long) -> Unit>,
) {
    CardDialog("Make a payment", onDismiss = {
        onHide()
        chosen.second(-1L)
    }) {
        val listState = rememberLazyListState()
        LazyRow(
            state = listState, modifier = Modifier.horizontalFadingEdge(
                listState, 16.dp, edgeColor = CardDefaults.elevatedCardColors().containerColor
            ), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(goals, key = { it.id }) {
                FilterChip(selected = it.id == chosen.first,
                    onClick = { chosen.second(if (chosen.first == it.id) -1L else it.id) },
                    label = {
                        Text(
                            it.name, modifier = Modifier.padding(8.dp)
                        )
                    })
            }
        }
        var amount by remember { mutableLongStateOf(0L) }
        OutlinedTextField(
            amount.toString(),
            { amount = it.toLongOrNull() ?: 0L },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = MaterialTheme.shapes.small,
            trailingIcon = {
                Icon(
                    Icons.Default.CurrencyRuble,
                    contentDescription = null,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        var comment by remember { mutableStateOf("") }
        OutlinedTextField(comment,
            onValueChange = { comment = it },
            placeholder = { Text("Add a comment") },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )
        val saveEnabled by remember(
            amount, chosen.first
        ) { mutableStateOf(amount > 0 && chosen.first != -1L) }
        Button(
            onClick = {
                if (chosen.first != -1L) {
                    if (amount != 0L) {
                        onCreate(chosen.first, amount, comment)
                        onHide()
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = saveEnabled
        ) { Text("Save") }
    }
}
