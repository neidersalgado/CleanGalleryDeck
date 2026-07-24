package com.deck.core.domain.repository

import com.deck.core.domain.model.ContentItem
import com.deck.core.domain.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun getItems(filter: MediaFilter? = null): Flow<List<ContentItem>>
    suspend fun deleteItem(item: ContentItem): Result<Unit>
    suspend fun keepItem(item: ContentItem): Result<Unit>
}