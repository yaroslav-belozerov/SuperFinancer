package com.yaabelozerov.superfinancer.finance.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.util.smartRound

@Composable
fun FinanceStats(uiState: FinanceState) {
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val progress by animateFloatAsState(uiState.totalAmount.div(uiState.totalGoal.toDouble()).takeUnless { it.isNaN() }?.toFloat() ?: 0.0f)
        Column {
            Text("Collected", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text("${uiState.totalAmount} of ${uiState.totalGoal} ₽")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(progress = { progress })
            Spacer(Modifier.height(4.dp))
            Text(
                "${progress.times(100).smartRound(1)}%",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}