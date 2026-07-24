package com.deck.core.domain.usecase

import com.deck.core.domain.model.ContentItem
import com.deck.core.domain.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetMediaItemsUseCase {
    suspend operator fun invoke(filter: MediaFilter? = null): Flow<List<ContentItem>>
}