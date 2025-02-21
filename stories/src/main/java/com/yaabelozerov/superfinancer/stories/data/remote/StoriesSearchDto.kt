package com.yaabelozerov.superfinancer.stories.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoriesSearchDto(
    val status: String,
    val copyright: String,
    val response: StoriesSearchResponseDto
)

@Serializable
data class StoriesSearchResponseDto(
    val docs: List<StoriesSearchResponseDataDto>
)

@Serializable
data class StoriesSearchResponseDataDto(
    @SerialName("web_url") val webUrl: String,
    val snippet: String,
    val multimedia: List<StoriesSearchResponseMultimediaDto>,
    val headline: StoriesSearchResponseHeadlineDto,
    val byline: StoriesSearchResponseBylineDto
)

@Serializable
data class StoriesSearchResponseMultimediaDto(
    val subtype: String,
    val url: String,
    val width: Int,
    val height: Int,
)

@Serializable
data class StoriesSearchResponseBylineDto(
    @SerialName("original") val original: String,
)

@Serializable
data class StoriesSearchResponseHeadlineDto(
    @SerialName("main") val main: String,
)
