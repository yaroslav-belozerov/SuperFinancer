package com.yaabelozerov.superfinancer.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.feed.domain.Post
import com.yaabelozerov.superfinancer.feed.domain.PostStory
import com.yaabelozerov.superfinancer.feed.domain.PostUseCase
import com.yaabelozerov.superfinancer.stories.StoriesToPostAdapter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val currentAttachedStory: PostStory? = null,
)

internal class FeedVM(
    private val storiesToPostAdapter: StoriesToPostAdapter = StoriesToPostAdapter(),
    private val useCase: PostUseCase = PostUseCase(),
) : ViewModel() {
    private val _state = MutableStateFlow(FeedUiState())
    val state = _state.asStateFlow()

    private val _favourites = MutableStateFlow<List<Post>>(emptyList())
    val favourites = _favourites.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.postFlow.collectLatest { posts ->
                _state.update { it.copy(posts = posts) }
            }
        }
        viewModelScope.launch {
            useCase.favouriteFlow.collectLatest { posts ->
                _favourites.update { posts }
            }
        }
    }

    fun createPost(contents: String, images: List<String>, articleUrl: String?, tags: List<String>) {
        viewModelScope.launch {
            useCase.createPost(
                contents, images, articleUrl, tags
            )
        }
    }

    fun setArticle(url: String?) {
        viewModelScope.launch {
            _state.update {
                it.copy(currentAttachedStory = url?.let {
                    storiesToPostAdapter.getCachedStoryByUrl(it).run {
                        PostStory(
                            title = first,
                            imageUrl = second,
                            url = third
                        )
                    }
                })
            }
        }
    }

    fun switchFavourite(it: Post) {
        viewModelScope.launch {
            useCase.switchFavourite(it)
        }
    }
}