package com.yaabelozerov.superfinancer.feed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.components.AsyncImageWithPlaceholder
import com.yaabelozerov.superfinancer.common.components.Header

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedScreen(articleUrl: String?, onAdd: () -> Unit, viewModel: FeedVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()
    var createPostOpen by remember { mutableStateOf(articleUrl != null) }
    LaunchedEffect(articleUrl) {
        viewModel.setArticle(articleUrl)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Header("Posts", Triple(
                "Create", Icons.Default.Add
            ) { createPostOpen = true })
        }
        items(uiState.posts) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (it.images.isNotEmpty()) FlowRow(
                        modifier = Modifier.clip(MaterialTheme.shapes.medium),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        it.images.forEach {
                            AsyncImageWithPlaceholder(
                                it.path, modifier = Modifier
                                    .sizeIn(
                                        minWidth = 48.dp,
                                        minHeight = 48.dp,
                                        maxWidth = 96.dp,
                                        maxHeight = 192.dp
                                    )
                                    .weight(1f), contentDescription = it.altText
                            )
                        }
                    }
                    Text(it.contents)
                    it.article?.let {
                        EmbeddedArticleCard(it)
                    }
                }
            }
        }
    }
    if (createPostOpen) CreatePostDialog(article = uiState.currentAttachedStory, onDismiss = {
        createPostOpen = false
        viewModel.setArticle(null)
        onAdd()
    }) { content, images ->
        viewModel.createPost(content, images, uiState.currentAttachedStory?.link)
    }
}