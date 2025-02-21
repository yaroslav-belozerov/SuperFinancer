package com.yaabelozerov.superfinancer.finance.ui.goal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.finance.domain.Goal
import com.yaabelozerov.superfinancer.finance.ui.FinanceVM
import kotlin.math.min

@Composable
internal fun GoalCard(goal: Goal, modifier: Modifier = Modifier, viewModel: FinanceVM) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(MaterialTheme.shapes.medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val progressFloat =
            min(
                goal.currentRubles.div(goal.maxRubles.toFloat()), 1.0f
            )
        val progress by animateFloatAsState(progressFloat)
        val color by animateColorAsState(
            when {
                progress < 0.25f -> MaterialTheme.colorScheme.error
                progress < 0.5f -> MaterialTheme.colorScheme.error.copy(0.5f).compositeOver(MaterialTheme.colorScheme.primary)
                progress < 0.75f -> MaterialTheme.colorScheme.error.copy(0.25f).compositeOver(MaterialTheme.colorScheme.primary)
                else -> MaterialTheme.colorScheme.primary
            }
        )
        GoalInfoLine(goal, progress, color, viewModel::onEvent)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                goal.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f, false)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "${goal.currentRubles} of ${
                    goal.maxRubles
                } ₽", maxLines = 1
            )
        }
    }
}