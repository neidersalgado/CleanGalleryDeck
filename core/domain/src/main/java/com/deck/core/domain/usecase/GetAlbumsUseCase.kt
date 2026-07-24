package com.deck.core.domain.usecase

import com.deck.core.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface GetAlbumsUseCase {
    suspend operator fun invoke(): Flow<List<Album>>
}