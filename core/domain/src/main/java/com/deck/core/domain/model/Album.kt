package com.deck.core.domain.model

data class Album(
    val id: String,
    val name: String,
    val coverItemId: String? = null,
    val itemCount: Int = 0,
    val sourceType: MediaSourceType = MediaSourceType.LOCAL_IMAGE
)