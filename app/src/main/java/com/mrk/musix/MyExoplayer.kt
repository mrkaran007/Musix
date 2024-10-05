package com.mrk.musix

import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.firestore.FirebaseFirestore
import com.mrk.musix.models.SongModel

object MyExoplayer {

    private var exoPlayer: ExoPlayer? = null
    private var currentSong: SongModel? = null

    fun getCurrentSong(): SongModel?{
        return currentSong
    }

    fun getInstance(): ExoPlayer? {
        return exoPlayer
    }

    fun startPlaying(context: android.content.Context, song: SongModel) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }

        // New song so start playing otherwise not..
        if (currentSong != song) {
            currentSong = song
            updateCount()
            currentSong?.url?.apply {
                val mediaItem = androidx.media3.common.MediaItem.fromUri(this)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }

    private fun updateCount() {
        currentSong?.id?.let {id->
            FirebaseFirestore.getInstance().collection("songs")
                .document(id)
                .get().addOnSuccessListener {
                    var latestCount = it.getLong("count")
                    if (latestCount == null) {
                        latestCount = 1
                    } else ++latestCount

                    FirebaseFirestore.getInstance().collection("songs")
                        .document(id)
                        .update(mapOf("count" to latestCount))
                }
        }
    }
}