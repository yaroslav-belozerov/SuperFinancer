package com.yaabelozerov.superfinancer.stories.ui

import com.yaabelozerov.superfinancer.stories.domain.Section

data class SectionUiState(
    val list: List<Section> = emptyList(),
    val selected: Section? = null,
)

