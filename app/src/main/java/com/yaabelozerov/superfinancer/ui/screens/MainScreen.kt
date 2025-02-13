package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.ui.viewmodel.MainVM

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun MainScreen(viewModel: MainVM = viewModel()) {
    val ticker by viewModel.tickerState.collectAsState()
    val sections by viewModel.sectionState.collectAsState()
    val storyFlow = viewModel.stories.collectAsLazyPagingItems()
    PullToRefreshBox(isRefreshing = ticker.isLoading, onRefresh = viewModel::fetchAll) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                ticker.error?.let {
                    var showDetails by remember { mutableStateOf(false) }
                    Card(
                        onClick = { showDetails = !showDetails },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .animateItem()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text("Some tickers failed to load")
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { showDetails = !showDetails }) {
                                Icon(
                                    if (showDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        AnimatedVisibility(showDetails) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                Text(
                                    it.localizedMessage ?: it.message
                                    ?: it.stackTrace.contentToString()
                                )
                            }
                        }
                    }
                }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ticker.map.entries.toList(), key = { it.key }) {
                        Card(modifier = Modifier.animateItem()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                AsyncImage(
                                    model = it.value.logoUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Text(it.value.name, fontWeight = FontWeight.Bold)
                                Text(it.value.run { "$value $currency" })
                            }
                        }
                    }
                }
            }
            item {
                var isExpanded by remember { mutableStateOf(false) }
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(true, onClick = {
                            isExpanded = !isExpanded
                        }, label = {
                            Text(
                                if (isExpanded) "Collapse" else "Expand",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }, trailingIcon = {
                            Icon(
                                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        })
                        sections.selected?.let {
                            FilterChip(false, onClick = {
                                viewModel.setSection(null)
                            }, label = {
                                Text(it.name, color = MaterialTheme.colorScheme.primary)
                            }, trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    AnimatedContent(isExpanded) { expandSectionList ->
                        if (expandSectionList) FlowRow(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            sections.list.forEach {
                                val selected = it.key == sections.selected?.key
                                FilterChip(selected, onClick = {
                                    viewModel.setSection(it)
                                }, label = { Text(it.name) }, leadingIcon = if (selected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                } else null)
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(sections.list) {
                                    val selected = it.key == sections.selected?.key
                                    FilterChip(selected, onClick = {
                                        viewModel.setSection(it)
                                    }, label = { Text(it.name) })
                                }
                            }
                        }
                    }
                }
            }
            items(storyFlow.itemCount) { index ->
                storyFlow[index]?.let { story ->
                    Card(
                        modifier = Modifier
                            .animateItem()
                            .fillParentMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box {
                                story.photoUrl?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.small)
                                            .fillMaxWidth(),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                                FilterChip(selected = true,
                                    onClick = {
                                        sections.list.find { it.name == story.sectionName }
                                            ?.let { viewModel.setSection(it) }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(vertical = 6.dp, horizontal = 12.dp),
                                    label = {
                                        Text(story.sectionName)
                                    })
                            }
                            Text(story.title, style = MaterialTheme.typography.headlineSmall)
                            story.description?.let {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}