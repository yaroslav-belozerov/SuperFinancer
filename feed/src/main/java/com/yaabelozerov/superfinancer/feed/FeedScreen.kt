package com.yaabelozerov.superfinancer.feed

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.AsyncImageWithPlaceholder
import com.yaabelozerov.superfinancer.common.components.Header
import com.yaabelozerov.superfinancer.common.local.AuthenticationManager
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.feed.ui.CreatePostDialog
import com.yaabelozerov.superfinancer.feed.ui.EmbeddedArticleCard
import com.yaabelozerov.superfinancer.feed.ui.FeedVM
import com.yaabelozerov.superfinancer.feed.ui.PostCard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    articleUrl: String?,
    onNavigateToFavs: () -> Unit,
    onAdd: () -> Unit,
    onClickArticle: (String) -> Unit,
) {
    val viewModel = viewModel<FeedVM>()
    val uiState by viewModel.state.collectAsState()
    var createPostOpen by remember { mutableStateOf(uiState.currentAttachedStory != null) }
    var showAuthModal by remember { mutableStateOf(false) }
    LaunchedEffect(articleUrl) {
        viewModel.setArticle(articleUrl)
        if (articleUrl != null) {
            createPostOpen = true
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Header(title = stringResource(R.string.posts), action = Triple(
                    first = stringResource(R.string.create), second = Icons.Default.Add
                ) { createPostOpen = true })
            }
            items(uiState.posts) {
                PostCard(it, onClickFavourite = {
                    viewModel.switchFavourite(it)
                }, onClickArticle)
            }
        }
        FloatingActionButton(elevation = FloatingActionButtonDefaults.loweredElevation(), modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd), onClick = { showAuthModal = true }) {
            Icon(Icons.Default.Star, contentDescription = null)
        }
    }
    if (showAuthModal) {
        var currentPassword by remember { mutableStateOf("") }
        val isPasswordSet by CommonModule.dataStoreManager.isKeySet(DataStoreManager.Keys.Strings.FAVOURITE_PASSWORD_HASH)
            .collectAsState(false)
        var isError by remember { mutableStateOf(false) }
        ModalBottomSheet(onDismissRequest = { showAuthModal = false }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)) {
                Header(if (isPasswordSet) stringResource(R.string.login) else stringResource(R.string.set_password), null)
                OutlinedTextField(currentPassword,
                    visualTransformation = PasswordVisualTransformation('*'),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { currentPassword = it; isError = false },
                    isError = isError,
                    shape = MaterialTheme.shapes.small,
                    placeholder = {
                        Text(stringResource(R.string.enter_your_password))
                    })
                val scope = rememberCoroutineScope()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        scope.launch {
                            CommonModule.authManager.tryAuth(key = DataStoreManager.Keys.Strings.FAVOURITE_PASSWORD_HASH,
                                value = currentPassword,
                                onResult = {
                                    when (it) {
                                        AuthenticationManager.Companion.PasswordResult.OK, AuthenticationManager.Companion.PasswordResult.UNSET -> {
                                            onNavigateToFavs()
                                        }

                                        AuthenticationManager.Companion.PasswordResult.WRONG -> isError =
                                            true
                                    }
                                })
                        }
                    }) { Text(stringResource(R.string.login)) }
                }
            }
        }
    }
    if (createPostOpen) CreatePostDialog(article = uiState.currentAttachedStory, onDismiss = {
        onAdd()
        viewModel.setArticle(null)
        createPostOpen = false
    }) { content, images, tags ->
        viewModel.createPost(content, images, uiState.currentAttachedStory?.url, tags)
    }
}