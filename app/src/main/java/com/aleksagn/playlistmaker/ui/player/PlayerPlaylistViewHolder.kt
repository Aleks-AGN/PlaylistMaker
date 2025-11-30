package com.aleksagn.playlistmaker.ui.player

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.PlayerPlaylistViewBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerPlaylistViewHolder(
    private val binding: PlayerPlaylistViewBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): PlayerPlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlayerPlaylistViewBinding.inflate(inflater, parent, false)
            return PlayerPlaylistViewHolder(binding, parent.context)
        }
    }

    fun bind(model: Playlist) {
        if (model.playlistImageUri != null && model.playlistImageUri.toString().isNotEmpty()) {
            Glide.with(itemView)
                .load(model.playlistImageUri)
                .fitCenter()
                .transform(
                    CenterCrop(),
                    RoundedCorners(dpToPx(2f, itemView.context))
                )
                .into(binding.image)
        }

        binding.title.text = model.playlistTitle

        if (model.trackCount > 0) {
            binding.count.text = model.trackCount.toString() + " " + pluralize(model.trackCount)
        } else {
            binding.count.text = ""
        }
    }

    private fun pluralize(count:Int): String {
        return when (count) {
            1 -> context.resources.getString(R.string.one_track)
            in 2..4 -> context.resources.getString(R.string.few_tracks)
            else -> context.resources.getString(R.string.many_tracks)
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
