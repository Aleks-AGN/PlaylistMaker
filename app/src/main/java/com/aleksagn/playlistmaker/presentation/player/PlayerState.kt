package com.aleksagn.playlistmaker.presentation.player

sealed interface  PlayerState {
    object PreparePlayer : PlayerState
    object StartPlayer : PlayerState
    object PausePlayer : PlayerState
    data class PlayingPlayer(val progressTime: String) : PlayerState
    object CompletePlayer : PlayerState
}