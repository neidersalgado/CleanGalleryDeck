package com.deck.core.domain.model

data class MediaFilter(
    val types: List<MediaType>? = null,
    val albumId: String? = null,
    val dateRange: Pair<Long, Long>? = null
)