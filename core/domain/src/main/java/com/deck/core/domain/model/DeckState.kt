package com.deck.core.domain.model

data class DeckState(
    val items: List<ContentItem> = emptyList(),
    val currentIndex: Int = 0,
    val reviewedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUndoVisible: Boolean = false,
    val lastDeletedItem: ContentItem? = null,
    val undoRemainingSeconds: Int = 5
)