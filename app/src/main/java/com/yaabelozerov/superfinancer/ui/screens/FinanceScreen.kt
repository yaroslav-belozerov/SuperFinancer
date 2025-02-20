package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.common.components.Header
import com.yaabelozerov.superfinancer.finance.R
import com.yaabelozerov.superfinancer.finance.domain.Goal
import com.yaabelozerov.superfinancer.finance.domain.Transaction
import com.yaabelozerov.superfinancer.finance.ui.CreateGoalDialog
import com.yaabelozerov.superfinancer.finance.ui.FinanceScreenEvent
import com.yaabelozerov.superfinancer.finance.ui.FinanceStats
import com.yaabelozerov.superfinancer.finance.ui.FinanceVM
import com.yaabelozerov.superfinancer.finance.ui.GoalCard
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun FinanceScreen(viewModel: FinanceVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()
    var createGoalVisible by remember { mutableStateOf(false) }
    var createTransactionVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FinanceStats(uiState)
        }
        item {
            Header("Goals", Triple("Add", Icons.Default.Add) { createGoalVisible = true })
        }
        items(uiState.goals, key = { "goal${it.id}" }) {
            GoalCard(
                it, modifier = Modifier.animateItem(), viewModel
            )
        }
        if (uiState.goals.isNotEmpty()) item {
            Header("Transactions", Triple(
                "Make", Icons.Default.AttachMoney
            ) { scope.launch { createTransactionVisible = true } })
        }
        items(uiState.transactions, key = { "transaction${it.id}" }) {
            Transaction(
                it, modifier = Modifier.animateItem()
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }


    if (createGoalVisible) CreateGoalDialog(onHide = {
        createGoalVisible = false
    }) { name, amount, image ->
        viewModel.onEvent(
            FinanceScreenEvent.CreateGoal(name, amount, image)
        )
    }
    if (createTransactionVisible) {
        var chosenGoalId by remember { mutableLongStateOf(-1L) }
        CreateTransactionModal(onHide = { createTransactionVisible = false },
            goals = uiState.goals,
            onCreate = { id, amount, comment ->
                viewModel.onEvent(
                    FinanceScreenEvent.MakeTransaction(
                        id, amount, comment
                    )
                )
            },
            chosen = chosenGoalId to { chosenGoalId = it })
    }
}

@Composable
private fun Transaction(transaction: Transaction, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                transaction.valueInRubles.toString().plus(" ₽"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(transaction.timestamp, fontStyle = FontStyle.Italic)
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
            Text(transaction.goalName, style = MaterialTheme.typography.titleLarge)
        }
        if (transaction.comment.isNotEmpty()) {
            Text(transaction.comment)
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
private fun CreateTransactionModal(
    onHide: () -> Unit,
    goals: List<Goal>,
    onCreate: (Long, Long, String) -> Unit,
    chosen: Pair<Long, (Long) -> Unit>,
) {
    Dialog(onDismissRequest = {
        onHide()
        chosen.second(-1L)
    }) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Make a payment", style = MaterialTheme.typography.headlineLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Button(
                    onClick = {
                        if (chosen.first != -1L) {
                            if (amount != 0L) {
                                onCreate(chosen.first, amount, comment)
                                onHide()
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) { Text("Save") }
            }
        }
    }
}