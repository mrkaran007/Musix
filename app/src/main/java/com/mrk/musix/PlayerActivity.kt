package com.mrk.musix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mrk.musix.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var exoPlayer: ExoPlayer

    var playerListener = object : Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MyExoplayer.getCurrentSong()?.apply {
            binding.songTitleTextView.text = title
            binding.songSubtitleTextView.text = subtitle
            Glide.with(binding.songCoverImageView).load(coverUrl)
                .circleCrop()
                .into(binding.songCoverImageView)

            Glide.with(binding.songGifImageView).load(R.drawable.ic_playing_song)
                .circleCrop()
                .into(binding.songGifImageView)

            // media exoplayer view know which song is playing
            exoPlayer = MyExoplayer.getInstance()!!
            binding.playerView.player = exoPlayer
            binding.playerView.showController()

            exoPlayer.addListener(playerListener)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.removeListener(playerListener)
    }

    fun showGif(show: Boolean) {
        if (show) {
            binding.songGifImageView.visibility = View.VISIBLE
        } else {
            binding.songGifImageView.visibility = View.INVISIBLE
        }
    }
}