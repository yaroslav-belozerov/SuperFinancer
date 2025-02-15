package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.ui.viewmodel.SearchVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SearchPopup(
    animatedContentScope: AnimatedContentScope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchVM = viewModel(),
) {
    val uiState by viewModel.state.collectAsState()
    val fr = remember { FocusRequester() }
    LaunchedEffect(Unit) { fr.requestFocus() }
    BackHandler {
        onBack()
        viewModel.onQueryChange("")
    }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            OutlinedTextField(
                uiState.query,
                onValueChange = viewModel::onQueryChange,
                shape = MaterialTheme.shapes.small,
                singleLine = true,
                placeholder = { Text("Search") },
                modifier = Modifier
                    .focusRequester(fr)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .sharedElement(
                        rememberSharedContentState("searchbar"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
        }
        items(uiState.searchResults) {
            ListItem(modifier = Modifier.animateItem(), headlineContent = {
                Text(it.title, style = MaterialTheme.typography.titleMedium)
            }, leadingContent = it.iconUrl?.let { url ->
                {
                    AsyncImage(
                        model = url, contentDescription = null, modifier = Modifier.size(48.dp).clip(
                            CircleShape), contentScale = ContentScale.Crop
                    )
                }
            }, trailingContent = { Text(it.type) }, supportingContent = { Text(it.description) })
        }
    }
}