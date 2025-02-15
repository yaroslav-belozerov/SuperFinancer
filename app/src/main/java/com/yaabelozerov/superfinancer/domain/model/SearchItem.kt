package com.yaabelozerov.superfinancer.domain.model

data class SearchItem(
    val type: SearchItemType,
    val title: String,
    val description: String,
    val iconUrl: String?,
    val uri: String
)

enum class SearchItemType(val string: String) {
    STORY("Story"), TICKER("Ticker");
}