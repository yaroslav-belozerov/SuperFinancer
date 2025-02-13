package com.yaabelozerov.superfinancer.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Story(
    val title: String,
    val description: String?,
    val author: String,
    val photoUrl: String?,
    val sectionName: String
)

@Serializable
data class StoriesDto(
    val status: String,
    val copyright: String,
    @SerialName("num_results") val numResults: Long,
    val results: List<StoryDto>,
)

@Serializable
data class StoryDto(
    @SerialName("slug_name") val slugName: String,
    val section: String,
    val subsection: String,
    val title: String,
    val abstract: String,
    val uri: String,
    val url: String,
    val byline: String,
    @SerialName("item_type") val itemType: String,
    val source: String,
    @SerialName("updated_date") val updatedDate: String,
    @SerialName("created_date") val createdDate: String,
    @SerialName("published_date") val publishedDate: String,
    @SerialName("first_published_date") val firstPublishedDate: String,
    @SerialName("material_type_facet") val materialTypeFacet: String,
    val kicker: String,
    @SerialName("subheadline") val subHeadline: String,
    @SerialName("des_facet") val desFacet: List<String>,
    @SerialName("org_facet") val orgFacet: List<String>,
    @SerialName("per_facet") val perFacet: List<String>,
    @SerialName("geo_facet") val geoFacet: List<String>,
    @SerialName("related_urls") val relatedUrls: List<StoryRelatedUrlDto>,
    val multimedia: List<StoryMultimediaDto>,
)

@Serializable
data class StoryMultimediaDto(
    val url: String,
    val format: String,
    val height: Long,
    val width: Long,
    val type: String,
    val subtype: String,
    val caption: String,
    val copyright: String,
)

@Serializable
data class StoryRelatedUrlDto(
    @SerialName("suggested_link_text") val suggestion: String,
    val url: String
)