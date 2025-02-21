package com.yaabelozerov.superfinancer.stories.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StoriesSearchDto(
    val status: String,
    val copyright: String,
    val response: StoriesSearchResponseDto
)

@Serializable
internal data class StoriesSearchResponseDto(
    val docs: List<StoriesSearchResponseDataDto>
)

@Serializable
internal data class StoriesSearchResponseDataDto(
    @SerialName("web_url") val webUrl: String,
    val snippet: String,
    val multimedia: List<StoriesSearchResponseMultimediaDto>,
    val headline: StoriesSearchResponseHeadlineDto,
    val byline: StoriesSearchResponseBylineDto,
    @SerialName("section_name") val sectionName: String,
    val source: String,
    @SerialName("pub_date") val createdDate: String
)

@Serializable
internal data class StoriesSearchResponseMultimediaDto(
    val subtype: String,
    val url: String,
    val width: Int,
    val height: Int,
)

@Serializable
internal data class StoriesSearchResponseBylineDto(
    @SerialName("original") val original: String,
)

@Serializable
internal data class StoriesSearchResponseHeadlineDto(
    @SerialName("main") val main: String,
)
