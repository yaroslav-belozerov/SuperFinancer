package com.yaabelozerov.superfinancer.finance.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.util.smartRound

@Composable
internal fun FinanceStats(uiState: FinanceState) {
    Spacer(Modifier.height(16.dp))
    val pager = rememberPagerState {
        if (uiState.stats.run { firstTransactionDate == null || lastTransactionDate == null }) 2 else 3
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(pager.pageCount) {
            val color by animateColorAsState(
                if (it == pager.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.25f)
            )
            Box(
                modifier = Modifier.size(20.dp, 4.dp).clip(MaterialTheme.shapes.medium).padding(horizontal = 4.dp).background(color)
            )
        }
    }
    HorizontalPager(pager, modifier = Modifier.height(88.dp)) { page ->
        when (page) {
            0 -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progress by animateFloatAsState(
                        uiState.totalAmount.div(uiState.totalGoal.toDouble())
                            .takeUnless { it.isNaN() }?.toFloat() ?: 0.0f
                    )
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

            1 -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Open goals", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = uiState.stats.openGoals.run { "$first with $second ₽ in total" },
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Closed goals", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = uiState.stats.closedGoals.run { "$first with $second ₽ in total" },
                        )
                    }
                }
            }

            2 -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.stats.firstTransactionDate?.let {
                        Column {
                            Text(
                                "First transaction", style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(it)
                        }
                    }
                    uiState.stats.lastTransactionDate?.let {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Last transaction", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(it)
                        }
                    }
                }
            }
        }
    }
}