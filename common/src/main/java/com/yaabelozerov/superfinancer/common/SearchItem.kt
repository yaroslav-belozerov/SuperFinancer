package com.yaabelozerov.superfinancer.common

data class SearchItem(
    val type: SearchItemType,
    val title: String,
    val description: String,
    val iconUrl: String?,
    val uri: String
)

enum class SearchItemType(val stringRes: Int) {
    STORY(R.string.story), TICKER(R.string.ticker);
}