package com.yaabelozerov.superfinancer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.generated.destinations.OpenStoryDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yaabelozerov.superfinancer.R
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.RefreshIndicator
import com.yaabelozerov.superfinancer.common.SearchItemType
import com.yaabelozerov.superfinancer.search.SearchPopup
import com.yaabelozerov.superfinancer.stories.storyItems
import com.yaabelozerov.superfinancer.tickers.TickerRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
) {
    val hasInternet by CommonModule.isNetworkAvailable.collectAsState()

    val listState = rememberLazyListState()

    var isSearching by remember { mutableStateOf(false) }
    val startingUp = stringResource(R.string.starting_up)
    var loadingTickers by remember { mutableStateOf(false to startingUp) }
    var onUpdateTickers by remember { mutableStateOf({}) }

    var loadingStories by remember { mutableStateOf(false) }
    var onUpdateStories by remember { mutableStateOf({}) }
    val storyList = storyItems(onClickStory = { navigator.navigate(OpenStoryDestination(it)) },
        setOnUpdateCallback = {
            onUpdateStories = it
        },
        onSetRefreshing = { loadingStories = it },
        listState = listState
    )

    val refreshState = rememberPullToRefreshState()
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(refreshState.distanceFraction >= 1f) {
        if (listState.isScrollInProgress) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    SharedTransitionLayout {
        AnimatedContent(isSearching) { searching ->
            if (searching) {
                SearchPopup(this@AnimatedContent, onBack = { isSearching = false }, onClick = {
                    when (it.type) {
                        SearchItemType.STORY -> navigator.navigate(OpenStoryDestination(it.uri))
                        SearchItemType.TICKER -> Unit
                    }
                })
            } else Box(
                Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = loadingTickers.first || loadingStories, onRefresh = {
                            onUpdateStories()
                            onUpdateTickers()
                        }, state = refreshState
                    )
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    item {
                        RefreshIndicator(
                            loadingTickers.first || loadingStories,
                            loadingTickers.second,
                            refreshState.distanceFraction,
                            modifier = Modifier.fillParentMaxWidth()
                        )
                    }
                    if (!hasInternet) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp, Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .alpha(0.5f)
                                )
                                Text(stringResource(R.string.no_internet_connection), modifier = Modifier.padding(16.dp))
                            }
                        }
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
                                Text(stringResource(R.string.search))
                            }
                        }
                    }
                    item {
                        TickerRow(setRefresh = { loading, lastUpdate ->
                            loadingTickers = loading to lastUpdate
                        }, setOnRefreshCallback = {
                            onUpdateTickers = it
                        })
                    }
                    storyList()
                }
            }
        }
    }
}