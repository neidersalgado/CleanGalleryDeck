package com.deck.core.domain.repository

import com.deck.core.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getAlbums(): Flow<List<Album>>
}