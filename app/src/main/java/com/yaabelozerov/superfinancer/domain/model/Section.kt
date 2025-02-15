package com.yaabelozerov.superfinancer.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Section(
    val name: String,
    val key: String
)
