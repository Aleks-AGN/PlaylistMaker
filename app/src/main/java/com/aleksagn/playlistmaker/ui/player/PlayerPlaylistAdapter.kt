package com.aleksagn.playlistmaker.ui.player

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.domain.models.Playlist

class PlayerPlaylistAdapter (
    private val playlists: List<Playlist>,
    private val listener: OnPlaylistItemClickListener
) : RecyclerView.Adapter<PlayerPlaylistViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        return PlayerPlaylistViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface OnPlaylistItemClickListener {
        fun onItemClick(position: Int)
    }
}
