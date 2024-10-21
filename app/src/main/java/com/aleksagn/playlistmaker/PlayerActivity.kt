package com.aleksagn.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<Toolbar>(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val cover = findViewById<ImageView>(R.id.cover)

        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val currentTrackTime = findViewById<TextView>(R.id.current_track_time)

        val trackTime = findViewById<TextView>(R.id.track_time)
        val collectionName = findViewById<TextView>(R.id.collection_name)
        val releaseDate = findViewById<TextView>(R.id.release_date)
        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name)
        val country = findViewById<TextView>(R.id.country)

        val intent = intent
        val jsonTrack = intent.getStringExtra("track").toString()

        val track = Gson().fromJson(jsonTrack, Track::class.java)

        trackName.text = track.trackName
        artistName.text = track.artistName

        trackTime.text = track.getFormatTime()
        currentTrackTime.text = trackTime.text

        if (track.collectionName.isNullOrEmpty()) {
            collectionName.isVisible = false
        } else {
            collectionName.text = track.collectionName
            collectionName.isVisible = true
        }

        releaseDate.text = track.releaseDate.substring(0, 4)

        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,this.applicationContext)))
            .into(cover)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
