package com.yaabelozerov.superfinancer.stories.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StoriesDto(
    val status: String,
    val copyright: String,
    @SerialName("num_results") val numResults: Long,
    val results: List<StoryDto>,
)

@Serializable
internal data class StoryDto(
    @SerialName("slug_name") val slugName: String,
    @SerialName("section") val section: String,
    val title: String,
    val abstract: String,
    val url: String,
    val byline: String,
    val source: String,
    @SerialName("updated_date") val updatedDate: String,
    @SerialName("created_date") val createdDate: String,
    @SerialName("first_published_date") val firstPublishedDate: String,
    @SerialName("subheadline") val subHeadline: String,
    val multimedia: List<StoryMultimediaDto>,
)

@Serializable
internal data class StoryMultimediaDto(
    val url: String,
    val width: Long,
)