package com.yaabelozerov.superfinancer.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.yaabelozerov.superfinancer.stories.ui.SectionList
import com.yaabelozerov.superfinancer.stories.ui.StoriesVM
import com.yaabelozerov.superfinancer.stories.ui.StoryCard
import kotlinx.coroutines.launch

@Composable
fun storyItems(
    onClickStory: (String) -> Unit,
    setOnUpdateCallback: (() -> Unit) -> Unit,
    onSetRefreshing: (Boolean) -> Unit,
    listState: LazyListState
): LazyListScope.() -> Unit {
    val viewModel = viewModel<StoriesVM>()
    val storyFlow = viewModel.stories.collectAsLazyPagingItems()
    val sections by viewModel.sectionState.collectAsState()
    val scope = rememberCoroutineScope()
    var refreshLoading by remember { mutableStateOf(false) }
    var appendLoading by remember { mutableStateOf(false) }
    LaunchedEffect(refreshLoading) {
        onSetRefreshing(refreshLoading)
    }
    LaunchedEffect(Unit) {
        setOnUpdateCallback {
            viewModel.fetchSections(true)
            storyFlow.refresh()
        }
    }
    LaunchedEffect(storyFlow.loadState.refresh) {
        refreshLoading = when (storyFlow.loadState.refresh) {
            is LoadState.Error, is LoadState.NotLoading -> false
            LoadState.Loading -> true
        }
        appendLoading = when (storyFlow.loadState.append) {
            is LoadState.Error, is LoadState.NotLoading -> false
            LoadState.Loading -> true
        }
    }
    return {
        item {
            SectionList(
                sections, { viewModel.setSection(it); scope.launch {
                    listState.animateScrollToItem(0)
                } }
            )
        }
        if (!refreshLoading) items(storyFlow.itemCount) { index ->
            storyFlow[index]?.let { story ->
                StoryCard(
                    story = story,
                    onClick = {
                        onClickStory(story.link)
                    },
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
                        .padding(48.dp)
                        .animateItem(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}