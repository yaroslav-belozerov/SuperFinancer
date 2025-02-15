package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.ui.viewmodel.MainVM

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun MainScreen(snackBarHostState: SnackbarHostState, viewModel: MainVM = viewModel()) {
    val ticker by viewModel.tickerState.collectAsState()
    val sections by viewModel.sectionState.collectAsState()
    val storyFlow = viewModel.stories.collectAsLazyPagingItems()
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
    Box(
        Modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = ticker.isLoading,
                onRefresh = viewModel::refreshAll,
                state = refreshState
            )
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()
        ) {
            item {
                RefreshIndicator(
                    ticker.isLoading,
                    ticker.lastUpdated,
                    refreshState.distanceFraction,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            item { TickerRow(ticker) }
            item { SectionList(sections, viewModel::setSection) }
            items(storyFlow.itemCount) { index ->
                storyFlow[index]?.let { story ->
                    StoryCard(
                        story = story,
                        onClickSectionName = viewModel::setSection,
                        modifier = Modifier
                            .animateItem()
                            .fillParentMaxWidth()
                    )
                }
            }
        }
    }
}