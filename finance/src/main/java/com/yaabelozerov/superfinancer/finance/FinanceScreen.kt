package com.yaabelozerov.superfinancer.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.components.Header
import com.yaabelozerov.superfinancer.finance.ui.FinanceScreenEvent
import com.yaabelozerov.superfinancer.finance.ui.FinanceStats
import com.yaabelozerov.superfinancer.finance.ui.FinanceVM
import com.yaabelozerov.superfinancer.finance.ui.goal.CreateGoalDialog
import com.yaabelozerov.superfinancer.finance.ui.goal.GoalCard
import com.yaabelozerov.superfinancer.finance.ui.transaction.CreateTransactionDialog
import com.yaabelozerov.superfinancer.finance.ui.transaction.Transaction
import kotlinx.coroutines.launch

@Composable
fun FinanceScreen() {
    val viewModel = viewModel<FinanceVM>()
    val uiState by viewModel.state.collectAsState()
    var createGoalVisible by remember { mutableStateOf(false) }
    var createTransactionVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp),
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
        if (uiState.goals.isNotEmpty() || uiState.transactions.isNotEmpty()) item {
            Header("Transactions", Triple(
                "Make", Icons.Default.AttachMoney
            ) { scope.launch { createTransactionVisible = true } })
        }
        items(uiState.transactions, key = { "transaction${it.id}" }) {
            Transaction(
                it, modifier = Modifier.animateItem(), viewModel
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }


    if (createGoalVisible) CreateGoalDialog(onHide = {
        createGoalVisible = false
    }) { name, amount, image, date ->
        viewModel.onEvent(
            FinanceScreenEvent.CreateGoal(name, amount, image, date)
        )
    }
    if (createTransactionVisible) {
        var chosenGoalId by remember { mutableLongStateOf(-1L) }
        CreateTransactionDialog(onHide = { createTransactionVisible = false },
            goals = uiState.goals,
            onCreate = { id, amount, comment ->
                viewModel.onEvent(
                    FinanceScreenEvent.MakeTransaction(
                        id, amount, comment, false
                    )
                )
            },
            chosen = chosenGoalId to { chosenGoalId = it })
    }
}