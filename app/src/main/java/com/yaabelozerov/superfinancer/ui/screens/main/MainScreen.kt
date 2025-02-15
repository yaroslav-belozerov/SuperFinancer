package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.ui.viewmodel.MainVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Destination<RootGraph>(start = true)
@Composable
fun MainScreen(snackBarHostState: SnackbarHostState, viewModel: MainVM = viewModel()) {
    var isSearching by remember { mutableStateOf(false) }
    val ticker by viewModel.tickerState.collectAsState()
    val sections by viewModel.sectionState.collectAsState()
    val storyFlow = viewModel.stories.collectAsLazyPagingItems()
    var refreshLoading by remember { mutableStateOf(true) }
    var appendLoading by remember { mutableStateOf(true) }
    LaunchedEffect(storyFlow.loadState.refresh) {
        refreshLoading = when (storyFlow.loadState.refresh) {
            is LoadState.Error -> false
            LoadState.Loading -> true
            is LoadState.NotLoading -> false
        }
        appendLoading = when (storyFlow.loadState.append) {
            is LoadState.Error -> false
            LoadState.Loading -> true
            is LoadState.NotLoading -> false
        }
    }
    LaunchedEffect(ticker.error) {
        ticker.error?.run {
            val action = snackBarHostState.showSnackbar(
                message = localizedMessage ?: message ?: stackTraceToString(),
                actionLabel = "Refresh",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
            when (action) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> viewModel.refreshAll()
            }
        }
    }
    val refreshState = rememberPullToRefreshState()
    val haptic = LocalHapticFeedback.current
    var firstTime by remember { mutableStateOf(true) }
    LaunchedEffect(refreshState.distanceFraction >= 1f) {
        if (!firstTime) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        firstTime = false
    }
    SharedTransitionLayout {
        AnimatedContent(isSearching) { searching ->
            if (searching) {
                SearchPopup(this@AnimatedContent, onBack = { isSearching = false })
            } else Box(
                Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = ticker.isLoading || (refreshLoading && firstTime),
                        onRefresh = {
                            viewModel.refreshAll()
                            storyFlow.refresh()
                        },
                        state = refreshState
                    )
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        RefreshIndicator(
                            ticker.isLoading || (refreshLoading && firstTime),
                            ticker.lastUpdated,
                            refreshState.distanceFraction,
                            modifier = Modifier.fillParentMaxWidth()
                        )
                    }
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            onClick = { isSearching = true },
                            modifier = Modifier
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .sharedElement(
                                    rememberSharedContentState("searchbar"),
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Search")
                            }
                        }
                    }
                    item { TickerRow(ticker) }
                    item { SectionList(sections, viewModel::setSection) }
                    if (!refreshLoading) items(storyFlow.itemCount) { index ->
                        storyFlow[index]?.let { story ->
                            StoryCard(
                                story = story,
                                onClickSectionName = viewModel::setSection,
                                modifier = Modifier
                                    .animateItem()
                                    .fillParentMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }
                    }
                    if (refreshLoading || appendLoading) {
                        item {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}