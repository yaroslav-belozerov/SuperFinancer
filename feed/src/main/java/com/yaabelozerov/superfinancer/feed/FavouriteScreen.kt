package com.yaabelozerov.superfinancer.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.components.Header
import com.yaabelozerov.superfinancer.feed.ui.FeedVM
import com.yaabelozerov.superfinancer.feed.ui.PostCard


@Composable
fun FavouriteScreen(onClickArticle: (String) -> Unit) {
    val viewModel = viewModel<FeedVM>()
    val posts by viewModel.favourites.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Header("Favourites", null)
        }
        items(posts) {
            PostCard(it, { viewModel.switchFavourite(it) }, onClickArticle)
        }
    }
}