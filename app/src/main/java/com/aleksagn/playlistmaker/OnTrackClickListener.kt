package com.aleksagn.playlistmaker

import android.content.Context

interface OnTrackClickListener {
    fun onTrackClick(track: Track, context: Context)
}
