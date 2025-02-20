package com.yaabelozerov.superfinancer.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.feed.domain.Post
import com.yaabelozerov.superfinancer.feed.domain.PostUseCase
import com.yaabelozerov.superfinancer.stories.domain.StoriesUseCase
import com.yaabelozerov.superfinancer.stories.domain.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val currentAttachedStory: Story? = null
)

class FeedVM(
    private val useCase: PostUseCase = PostUseCase(),
    private val storiesUseCase: StoriesUseCase = StoriesUseCase()
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

    fun createPost(contents: String, images: List<Pair<String, String>>, articleUrl: String?) {
        viewModelScope.launch {
            useCase.createPost(
                contents, images, articleUrl
            )
        }
    }

    fun setArticle(url: String?) {
        viewModelScope.launch {
            _state.update { it.copy(currentAttachedStory = url?.let { storiesUseCase.getCachedStoryByUrl(it) }) }
        }
    }
}