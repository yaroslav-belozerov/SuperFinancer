package com.yaabelozerov.superfinancer.domain.model

data class SearchItem(
    val type: String,
    val title: String,
    val description: String,
    val iconUrl: String?,
)