package com.yaabelozerov.superfinancer.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.R
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.ui.toString
import com.yaabelozerov.superfinancer.ui.viewmodel.FinanceVM
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination<RootGraph>
@Composable
fun FinanceScreen(viewModel: FinanceVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()
    val createGoalState = rememberModalBottomSheetState()
    val createTransactionState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                "Goals", "Add", Icons.Default.Add
            ) { scope.launch { createGoalState.show() } }
        }
        items(uiState.goals, key = { "goal${it.id}" }) { Goal(it) }
        item {
            Header(
                "Transactions", "Make", Icons.Default.AttachMoney
            ) { scope.launch { createTransactionState.show() } }
        }
        items(uiState.transactions, key = { "transaction${it.id}" }) { Transaction(it) }
        item { Spacer(Modifier.height(16.dp)) }
    }


    if (createGoalState.isVisible) CreateGoalModal(createGoalState, viewModel::createGoal)
    if (createTransactionState.isVisible) CreateTransactionModal(
        createTransactionState, uiState.goals, viewModel::makeTransaction
    )
}

@Composable
private fun Header(title: String, actionName: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        Button(onClick = onClick) {
            Icon(icon, contentDescription = null)
            Text(actionName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGoalModal(state: SheetState, onCreate: (String, Double, String) -> Unit) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = { scope.launch { state.hide() } }) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create a goal", style = MaterialTheme.typography.headlineLarge)
            var currentImage by remember { mutableStateOf<String?>(null) }
            var currentImageUri by remember { mutableStateOf<Uri?>(null) }
            val picker =
                rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    uri?.let { uriNotNull ->
                        currentImageUri = uriNotNull
                        scope.launch {
                            Application.mediaManager.importMedia(uriNotNull) {
                                currentImage?.let { current ->
                                    scope.launch {
                                        Application.mediaManager.removeMedia(current)
                                    }
                                }
                                currentImage = it
                            }
                        }
                    }
                }
            AsyncImage(model = currentImageUri
                ?: (if (isSystemInDarkTheme()) R.drawable.image_placeholder_dark else R.drawable.image_placeholder_light),
                contentDescription = null,
                contentScale = if (currentImageUri == null) ContentScale.Fit else ContentScale.Crop,
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
            TextField(name,
                onValueChange = { name = it },
                placeholder = { Text("Name your goal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            var amount by remember { mutableDoubleStateOf(0.0) }
            TextField(
                amount.toString(2),
                { amount = it.toDoubleOrNull() ?: 0.0 },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Icon(
                        Icons.Default.CurrencyRuble,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (amount > 0 && name.isNotBlank()) {
                        onCreate(name, amount, currentImage ?: "")
                        scope.launch { state.hide() }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}

@Composable
fun Goal(goal: Goal) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = goal.image,
                contentDescription = null,
                modifier = Modifier
                    .height(192.dp)
                    .weight(0.8f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            val progress by animateFloatAsState(
                min(
                    goal.currentRubles.div(goal.maxRubles), 1.0
                ).toFloat()
            )
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
                        .background(MaterialTheme.colorScheme.primary)
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
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                goal.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f, false)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "${goal.currentRubles.toString(2)} of ${
                    goal.maxRubles.toString(
                        2
                    )
                } ₽", maxLines = 1
            )
        }
    }
}

@Composable
private fun Transaction(transaction: Transaction) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                transaction.valueInRubles.toString(2).plus(" ₽"),
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionModal(
    state: SheetState,
    goals: List<Goal>,
    onCreate: (Long, Double, String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = { scope.launch { state.hide() } }) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Make a payment", style = MaterialTheme.typography.headlineLarge)
            var chosenGoalId by remember { mutableStateOf<Long?>(null) }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(goals, key = { it.id }) {
                    FilterChip(selected = it.id == chosenGoalId,
                        onClick = { chosenGoalId = it.id },
                        label = {
                            Text(
                                it.name, modifier = Modifier.padding(8.dp)
                            )
                        })
                }
            }
            var amount by remember { mutableDoubleStateOf(0.0) }
            TextField(
                amount.toString(2),
                { amount = it.toDoubleOrNull() ?: 0.0 },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Icon(
                        Icons.Default.CurrencyRuble,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            var comment by remember { mutableStateOf("") }
            TextField(comment,
                onValueChange = { comment = it },
                placeholder = { Text("Add a comment") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    chosenGoalId?.let {
                        onCreate(it, amount, comment)
                        scope.launch { state.hide() }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}