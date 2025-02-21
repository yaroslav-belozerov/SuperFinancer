package com.yaabelozerov.superfinancer.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.finance.domain.Goal
import kotlin.math.roundToInt

@Composable
fun GoalInfoLine(goal: Goal, progress: Float, indicatorColor: Color, onEvent: (FinanceScreenEvent) -> Unit) =
    if (goal.image.isBlank()) GoalInfoLineWithoutImage(goal, progress, indicatorColor, onEvent) else GoalInfoLineWithImage(goal, progress, indicatorColor, onEvent)

@Composable
private fun GoalInfoLineWithoutImage(
    goal: Goal,
    progress: Float,
    color: Color,
    onEvent: (FinanceScreenEvent) -> Unit,
) {
    var optionsOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { optionsOpen = true }, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceBright)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp),
            ) {
                Text(
                    "${(progress * 100).roundToInt()}%",
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    GoalOptionRow(goal, onEvent, optionsOpen) { optionsOpen = false }
}

@Composable
private fun GoalInfoLineWithImage(goal: Goal, progress: Float, color: Color, onEvent: (FinanceScreenEvent) -> Unit) {
    var optionsOpen by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .height(192.dp)
                .weight(0.8f)
                .clip(MaterialTheme.shapes.medium),
        ) {
            AsyncImage(
                model = goal.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            GoalOptionRow(goal, onEvent, optionsOpen) { optionsOpen = false }
            if (!optionsOpen) {
                FilledIconButton(onClick = { optionsOpen = true }) {
                    Icon(
                        Icons.Default.Edit, contentDescription = "edit goal"
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceBright)
                .height(192.dp)
                .weight(0.2f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(progress)
                    .align(Alignment.BottomCenter)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    "${(progress * 100).roundToInt()}%",
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
