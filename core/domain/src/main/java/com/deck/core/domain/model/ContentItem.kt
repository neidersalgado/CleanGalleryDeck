package com.deck.core.domain.model

import android.net.Uri

data class ContentItem(
    val id: String,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val dateTaken: Long,
    val bucketId: String? = null,
    val albumName: String? = null,
    val mediaType: MediaType,
    val duration: Long? = null,
    val sourceType: MediaSourceType
)