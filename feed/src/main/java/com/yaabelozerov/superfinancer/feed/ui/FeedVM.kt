package com.yaabelozerov.superfinancer.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.feed.domain.Post
import com.yaabelozerov.superfinancer.feed.domain.PostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Post> = emptyList()
)

class FeedVM(
    private val useCase: PostUseCase = PostUseCase()
): ViewModel() {
    private val _state = MutableStateFlow(FeedUiState())
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            useCase.postFlow.collectLatest { posts ->
                _state.update { it.copy(posts = posts) }
            }
        }
    }

    fun createPost(contents: String, images: List<Pair<String, String>>, articleSlug: String?) {
        viewModelScope.launch {
            useCase.createPost(
                contents, images, articleSlug
            )
        }
    }
}