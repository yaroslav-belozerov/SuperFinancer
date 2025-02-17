package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.R
import com.yaabelozerov.superfinancer.ui.toString
import com.yaabelozerov.superfinancer.ui.viewmodel.FinanceVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination<RootGraph>
@Composable
fun FinanceScreen(viewModel: FinanceVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()
    val createGoalState = rememberModalBottomSheetState()
    val createTransactionState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Goals", style = MaterialTheme.typography.headlineLarge)
                Button(onClick = { scope.launch { createGoalState.show() } }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Add")
                }
            }
        }
        items(uiState.goals, key = { "goal${it.id}" }) {
            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        it.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f, false)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "${it.currentRubles.toString(2)} of ${
                            it.maxRubles.toString(
                                2
                            )
                        } ₽", maxLines = 1
                    )
                }
                LinearProgressIndicator(
                    progress = { it.currentRubles.div(it.maxRubles).toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Transactions", style = MaterialTheme.typography.headlineLarge)
                Button(onClick = { scope.launch { createTransactionState.show() } }) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                    Text("Make")
                }
            }
        }
        items(uiState.transactions, key = { "transaction${it.id}" }) {
            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        it.valueInRubles.toString(2).plus(" ₽"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(it.timestamp, fontStyle = FontStyle.Italic)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.right_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(it.goalName, style = MaterialTheme.typography.titleLarge)
                }
                if (it.comment.isNotEmpty()) {
                    Text(it.comment)
                }
            }
        }
    }
    if (createGoalState.isVisible) ModalBottomSheet(onDismissRequest = { scope.launch { createGoalState.hide() } }) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create a goal", style = MaterialTheme.typography.headlineLarge)
            var name by remember { mutableStateOf("") }
            TextField(name,
                onValueChange = { name = it },
                placeholder = { Text("Name your goal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            var amount by remember { mutableDoubleStateOf(0.0) }
            TextField(
                amount.toString(2),
                { amount = it.toDoubleOrNull() ?: 0.0 },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Icon(
                        Icons.Default.CurrencyRuble,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.createGoal(name, amount)
                    scope.launch { createGoalState.hide() }
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
    if (createTransactionState.isVisible) ModalBottomSheet(onDismissRequest = { scope.launch { createTransactionState.hide() } }) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Make a payment", style = MaterialTheme.typography.headlineLarge)
            var chosenGoalId by remember { mutableStateOf<Long?>(null) }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.goals, key = { it.id }) {
                    FilterChip(selected = it.id == chosenGoalId,
                        onClick = { chosenGoalId = it.id },
                        label = {
                            Text(
                                it.name, modifier = Modifier.padding(8.dp)
                            )
                        })
                }
            }
            var amount by remember { mutableDoubleStateOf(0.0) }
            TextField(
                amount.toString(2),
                { amount = it.toDoubleOrNull() ?: 0.0 },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Icon(
                        Icons.Default.CurrencyRuble,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            var comment by remember { mutableStateOf("") }
            TextField(comment,
                onValueChange = { comment = it },
                placeholder = { Text("Add a comment") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    chosenGoalId?.let {
                        viewModel.makeTransaction(it, amount, comment)
                        scope.launch { createGoalState.hide() }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}