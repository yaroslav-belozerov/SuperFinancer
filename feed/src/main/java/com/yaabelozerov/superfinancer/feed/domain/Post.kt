package com.yaabelozerov.superfinancer.feed.domain

internal data class Post(
    val id: Long,
    val contents: String,
    val images: List<PostImage>,
    val article: PostStory?
)

internal data class PostImage(
    val path: String,
    val altText: String
)

internal data class PostStory(
    val url: String,
    val imageUrl: String?,
    val title: String
)