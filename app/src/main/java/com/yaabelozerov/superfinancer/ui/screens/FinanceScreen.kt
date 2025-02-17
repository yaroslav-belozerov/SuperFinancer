package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.ui.viewmodel.FinanceVM

@Destination<RootGraph>
@Composable
fun FinanceScreen(viewModel: FinanceVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()

    LazyColumn {
        items(uiState.goals) {
            Text(it.name)
            Text(it.maxRubles)
            LinearProgressIndicator(progress = { it.completion.toFloat() })
        }
    }
}