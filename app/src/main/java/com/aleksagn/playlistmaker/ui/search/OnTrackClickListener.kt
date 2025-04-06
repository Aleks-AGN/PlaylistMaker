package com.aleksagn.playlistmaker.ui.search

import android.content.Context
import com.aleksagn.playlistmaker.domain.models.Track

interface OnTrackClickListener {
    fun onTrackClick(track: Track, context: Context)
}
