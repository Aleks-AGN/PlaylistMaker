package com.aleksagn.playlistmaker.ui.player.view_model

sealed interface  PlayerState {
    object PreparePlayer : PlayerState
    object StartPlayer : PlayerState
    object PausePlayer : PlayerState
    data class PlayingPlayer(val progressTime: String) : PlayerState
    object CompletePlayer : PlayerState
}