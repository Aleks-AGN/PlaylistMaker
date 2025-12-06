package com.aleksagn.playlistmaker.ui.library

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.domain.models.Track

class PlaylistTracksAdapter (
    private val tracks: List<Track>,
    private val listener: OnTrackItemClickListener,
    private val longListener: OnTrackItemLongClickListener,
) : RecyclerView.Adapter<PlaylistTracksViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksViewHolder {
        return PlaylistTracksViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlaylistTracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            listener.onTrackItemClick(position)
        }
        holder.itemView.setOnLongClickListener {
            longListener.onTrackItemLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface OnTrackItemClickListener {
        fun onTrackItemClick(position: Int)
    }

    fun interface OnTrackItemLongClickListener {
        fun onTrackItemLongClick(position: Int)
    }
}
