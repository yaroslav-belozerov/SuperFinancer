package com.yaabelozerov.superfinancer.finance.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButton
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import com.yaabelozerov.superfinancer.finance.domain.Goal
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun GoalCard(goal: Goal, modifier: Modifier = Modifier, viewModel: FinanceVM) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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

@Composable
fun CreateGoalDialog(onHide: () -> Unit, onCreate: (String, Long, String) -> Unit) {
    val scope = rememberCoroutineScope()
    var imagePath by remember { mutableStateOf<String?>(null) }
    Dialog(onDismissRequest = {
        scope.launch {
            onHide()
            imagePath?.let {
                CommonModule.mediaManager.removeMedia(it)
            }
        }
    }) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Create a goal", style = MaterialTheme.typography.headlineLarge)
                }
                imagePath?.let {
                    PhotoPickerImage(it) { imagePath = null }
                } ?: PhotoPickerButton { imagePath = it }
                var name by remember { mutableStateOf("") }
                OutlinedTextField(name,
                    onValueChange = { name = it },
                    placeholder = { Text("Name your goal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                var amount by remember { mutableLongStateOf(0L) }
                OutlinedTextField(
                    amount.toString(),
                    { amount = it.toLongOrNull() ?: 0L },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    trailingIcon = {
                        Icon(
                            Icons.Default.CurrencyRuble,
                            contentDescription = null,
                        )
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (amount > 0 && name.isNotBlank()) {
                            onCreate(name, amount, imagePath ?: "")
                            onHide()
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) { Text("Save") }
            }
        }
    }
}
