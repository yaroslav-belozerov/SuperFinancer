package com.yaabelozerov.superfinancer.finance.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.finance.domain.Goal

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalOptionRow(
    goal: Goal,
    onEvent: (FinanceScreenEvent) -> Unit,
    isOpen: Boolean,
    onClose: () -> Unit,
) = AnimatedContent(isOpen, transitionSpec = { fadeIn() togetherWith fadeOut() }) { open ->
    Surface(
        color = if (open) MaterialTheme.colorScheme.surfaceBright else Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
    ) {
        if (open) FlowColumn(modifier = Modifier.padding(12.dp)) {
            Button(onClick = {
                onEvent(FinanceScreenEvent.DeleteGoal(goal))
                onClose()
            }) {
                Icon(Icons.Default.CheckCircle, contentDescription = "close goal")
                Spacer(Modifier.width(8.dp))
                Text("Close Goal")
            }
            OutlinedButton(onClick = {
                onClose()
            }) { Text("Cancel") }
        }
    }
}
