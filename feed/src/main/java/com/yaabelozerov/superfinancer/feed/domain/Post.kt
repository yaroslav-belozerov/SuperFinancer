package com.yaabelozerov.superfinancer.feed.domain

internal data class Post(
    val id: Long,
    val contents: String,
    val images: List<String>,
    val article: PostStory?,
    val tags: List<String>,
    val isFavourite: Boolean
)

internal data class PostStory(
    val url: String,
    val imageUrl: String?,
    val title: String
)