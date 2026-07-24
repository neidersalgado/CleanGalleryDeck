package com.deck.core.domain.usecase

import com.deck.core.domain.model.ContentItem

interface KeepMediaUseCase {
    suspend operator fun invoke(item: ContentItem): Result<Unit>
}