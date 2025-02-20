package com.yaabelozerov.superfinancer.feed.domain

data class Post(
    val id: Long,
    val contents: String,
    val images: List<PostImage>,
    val article: PostArticle?
)

data class PostImage(
    val path: String,
    val altText: String
)

data class PostArticle(
    val slug: String,
    val title: String
)