package com.deck.core.domain.repository

import com.deck.core.domain.model.ContentItem
import kotlinx.coroutines.flow.Flow

interface GooglePhotosRepository {
    suspend fun authenticate(): Result<Unit>
    suspend fun getPickerItems(): Flow<List<ContentItem>>
    suspend fun deleteItem(item: ContentItem): Result<Unit>
}