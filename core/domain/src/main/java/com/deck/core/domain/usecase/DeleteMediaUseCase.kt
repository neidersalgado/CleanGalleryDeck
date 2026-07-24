package com.deck.core.domain.usecase

import com.deck.core.domain.model.ContentItem

interface DeleteMediaUseCase {
    suspend operator fun invoke(item: ContentItem): Result<Unit>
}