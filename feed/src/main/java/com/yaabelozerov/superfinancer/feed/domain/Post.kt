package com.yaabelozerov.superfinancer.feed.domain

import com.yaabelozerov.superfinancer.stories.domain.Story

data class Post(
    val id: Long,
    val contents: String,
    val images: List<PostImage>,
    val article: Story?
)

data class PostImage(
    val path: String,
    val altText: String
)