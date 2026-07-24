package com.deck.core.domain.usecase

import com.deck.core.domain.model.ContentItem
import kotlinx.coroutines.flow.Flow

interface SyncGooglePhotosUseCase {
    suspend operator fun invoke(): Flow<List<ContentItem>>
}