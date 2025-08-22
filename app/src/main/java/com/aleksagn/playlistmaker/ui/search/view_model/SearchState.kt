package com.aleksagn.playlistmaker.ui.search.view_model

import com.aleksagn.playlistmaker.domain.models.Track

interface SearchState {
    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState
}