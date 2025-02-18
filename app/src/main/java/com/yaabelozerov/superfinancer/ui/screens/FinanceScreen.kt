package com.yaabelozerov.superfinancer.ui.screens

import android.net.Uri
import android.text.BoringLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.R
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.ui.App
import com.yaabelozerov.superfinancer.ui.smartRound
import com.yaabelozerov.superfinancer.ui.toString
import com.yaabelozerov.superfinancer.ui.viewmodel.FinanceScreenEvent
import com.yaabelozerov.superfinancer.ui.viewmodel.FinanceVM
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
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
        item {
            Header("Goals",
                Triple("Add", Icons.Default.Add) { scope.launch { createGoalState.expand() } })
        }
        items(uiState.goals, key = { "goal${it.id}" }) {
            Goal(
                it, modifier = Modifier.animateItem(), viewModel
            )
        }
        if (uiState.goals.isNotEmpty()) item {
            Header("Transactions", Triple(
                "Make", Icons.Default.AttachMoney
            ) { scope.launch { createTransactionState.expand() } })
        }
        items(uiState.transactions, key = { "transaction${it.id}" }) {
            Transaction(
                it, modifier = Modifier.animateItem()
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }


    if (createGoalState.isVisible) CreateGoalModal(createGoalState) { name, amount, image ->
        viewModel.onEvent(
            FinanceScreenEvent.CreateGoal(name, amount, image)
        )
    }
    if (createTransactionState.isVisible) {
        var chosenGoalId by remember { mutableLongStateOf(-1L) }
        CreateTransactionModal(createTransactionState, uiState.goals, { id, amount, comment ->
            viewModel.onEvent(
                FinanceScreenEvent.MakeTransaction(
                    id, amount, comment
                )
            )
        }, chosen = chosenGoalId to { chosenGoalId = it })
    }
}

@Composable
private fun Header(
    title: String,
    action: Triple<String, ImageVector, () -> Unit>? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        action?.let { (name, icon, action) ->
            Button(onClick = action) {
                Icon(icon, contentDescription = null)
                Text(name)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGoalModal(state: SheetState, onCreate: (String, Long, String) -> Unit) {
    val scope = rememberCoroutineScope()
    var currentImage by remember { mutableStateOf<String?>(null) }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    Dialog(onDismissRequest = {
        scope.launch {
            state.hide()
            currentImage?.let {
                Application.mediaManager.removeMedia(it)
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
                            scope.launch { state.hide() }
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) { Text("Save") }
            }
        }
    }
}

@Composable
fun Goal(goal: Goal, modifier: Modifier = Modifier, viewModel: FinanceVM) {
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
        if (goal.image.isNotBlank()) GoalLineWithImage(goal, progress, viewModel::onEvent)
        else GoalLineWithoutImage(goal, progress, viewModel::onEvent)
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
private fun GoalLineWithoutImage(
    goal: Goal,
    progress: Float,
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
                    .background(MaterialTheme.colorScheme.primary)
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
private fun GoalLineWithImage(goal: Goal, progress: Float, onEvent: (FinanceScreenEvent) -> Unit) {
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

@Composable
private fun Transaction(transaction: Transaction, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                transaction.valueInRubles.toString().plus(" ₽"),
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
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionModal(
    state: SheetState,
    goals: List<Goal>,
    onCreate: (Long, Long, String) -> Unit,
    chosen: Pair<Long, (Long) -> Unit>,
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = {
        scope.launch { state.hide() }
        chosen.second(-1L)
    }) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Make a payment", style = MaterialTheme.typography.headlineLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(goals, key = { it.id }) {
                        FilterChip(selected = it.id == chosen.first,
                            onClick = { chosen.second(if (chosen.first == it.id) -1L else it.id) },
                            label = {
                                Text(
                                    it.name, modifier = Modifier.padding(8.dp)
                                )
                            })
                    }
                }
                var amount by remember { mutableLongStateOf(0L) }
                OutlinedTextField(
                    amount.toString(),
                    { amount = it.toLongOrNull() ?: 0L },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = MaterialTheme.shapes.small,
                    trailingIcon = {
                        Icon(
                            Icons.Default.CurrencyRuble,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                var comment by remember { mutableStateOf("") }
                OutlinedTextField(comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Add a comment") },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (chosen.first != -1L) {
                            if (amount != 0L) {
                                onCreate(chosen.first, amount, comment)
                                scope.launch { state.hide() }
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) { Text("Save") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GoalOptionRow(
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
            }) { Text("Delete") }
            OutlinedButton(onClick = {
                onClose()
            }) { Text("Cancel") }
        }
    }
}