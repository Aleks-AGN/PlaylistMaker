package com.aleksagn.playlistmaker.presentation.library

import com.aleksagn.playlistmaker.domain.models.Track

sealed interface FavoritesState {

    object Empty : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState
}