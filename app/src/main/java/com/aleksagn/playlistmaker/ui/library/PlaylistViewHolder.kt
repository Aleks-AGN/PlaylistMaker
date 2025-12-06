package com.aleksagn.playlistmaker.ui.library

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.PlaylistViewBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistViewBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding, parent.context)
        }
    }

    fun bind(model: Playlist) {
        if (model.playlistImageUri != null && model.playlistImageUri.toString().isNotEmpty()) {
            Glide.with(itemView)
                .load(model.playlistImageUri)
                .fitCenter()
                .transform(
                    CenterCrop(),
                    RoundedCorners(dpToPx(8f, itemView.context))
                )
                .into(binding.image)
        }

        binding.title.text = model.playlistTitle

        binding.count.text = model.trackCount.toString() + " " + context.resources.getQuantityString(R.plurals.plural_tracks, model.trackCount)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
