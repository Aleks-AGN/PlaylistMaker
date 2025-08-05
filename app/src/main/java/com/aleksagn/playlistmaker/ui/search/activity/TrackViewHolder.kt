package com.aleksagn.playlistmaker.ui.search.activity

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.TrackViewBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(private val binding: TrackViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }

    fun bind(model: Track) {
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.getFormatTime()

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(binding.trackLogo)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
