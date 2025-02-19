package com.yaabelozerov.superfinancer.finance.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.common.CommonModule
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
        val progress by animateFloatAsState(
            min(
                goal.currentRubles.div(goal.maxRubles.toFloat()), 1.0f
            )
        )
        GoalInfoLine(goal, progress, viewModel::onEvent)
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
    var currentImage by remember { mutableStateOf<String?>(null) }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    Dialog(onDismissRequest = {
        scope.launch {
            onHide()
            currentImage?.let {
                CommonModule.mediaManager.removeMedia(it)
            }
        }
    }) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val picker =
                    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                        uri?.let { uriNotNull ->
                            currentImageUri = uriNotNull
                            scope.launch {
                                CommonModule.mediaManager.importMedia(uriNotNull) {
                                    currentImage?.let { current ->
                                        scope.launch {
                                            CommonModule.mediaManager.removeMedia(current)
                                        }
                                    }
                                    currentImage = it
                                }
                            }
                        }
                    }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Create a goal", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.width(16.dp))
                    if (currentImageUri == null) IconButton(onClick = {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) { Icon(Icons.Default.ImageSearch, contentDescription = null) }
                }
                if (currentImageUri != null) AsyncImage(model = currentImageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(192.dp)
                        .aspectRatio(1.5f)
                        .clip(
                            MaterialTheme.shapes.medium
                        )
                        .clickable {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        })
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
                            onCreate(name, amount, currentImage ?: "")
                            onHide()
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) { Text("Save") }
            }
        }
    }
}
