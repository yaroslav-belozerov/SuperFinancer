package com.yaabelozerov.superfinancer.stories.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yaabelozerov.superfinancer.stories.domain.Section
import com.yaabelozerov.superfinancer.stories.domain.StoriesUseCase
import com.yaabelozerov.superfinancer.stories.domain.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class StoriesVM(
    private val storyUseCase: StoriesUseCase = StoriesUseCase(),
): ViewModel() {
    private val _sectionUiState = MutableStateFlow(SectionUiState())
    val sectionState = _sectionUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var stories: Flow<PagingData<Story>> = _sectionUiState.flatMapLatest { currentState ->
        storyUseCase.createFlow(currentState.selected)
    }.cachedIn(viewModelScope)

    init {
        fetchSections()
    }

    fun fetchSections(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedSections = StoriesUseCase.getSavedSections().first()
            val sections = if (forceRefresh || savedSections == null) {
                storyUseCase.getSections { StoriesUseCase.setSavedSections(it) }
            } else savedSections
            (sections.ifEmpty { savedSections.orEmpty() }).let {
                _sectionUiState.update { st ->
                    st.copy(list = it,
                        selected = it.firstOrNull { _sectionUiState.value.selected?.key == it.key })
                }
            }
        }
    }

    fun setSection(section: Section?) {
        if (section?.key == _sectionUiState.value.selected?.key) {
            _sectionUiState.update { it.copy(selected = null) }
        } else {
            _sectionUiState.update { it.copy(selected = section) }
        }
    }

    fun setSection(sectionName: String) {
        _sectionUiState.value.list.firstOrNull { it.name == sectionName }?.let { setSection(it) }
    }
}