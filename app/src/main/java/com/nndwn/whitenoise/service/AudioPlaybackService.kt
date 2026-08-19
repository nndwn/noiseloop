package com.nndwn.whitenoise.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nndwn.whitenoise.MainActivity
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.data.extensions.asMediaItem
import com.nndwn.whitenoise.data.repository.AudioRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlaybackService : MediaSessionService() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var audioRepository : AudioRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val player = initializePlayer()
        val pendingIntent = createSessionActivityIntent()
        val sessionCallback = createMediaSessionCallback(player)

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .setCallback(sessionCallback)
            .build()
    }

    private fun initializePlayer(): ExoPlayer {
        return ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    private fun createSessionActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("OPEN_NOW_PLAYING", true)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @OptIn(UnstableApi::class)
    private fun createMediaSessionCallback(player: Player): MediaSession.Callback {
        return object : MediaSession.Callback {

            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                return MediaSession.ConnectionResult.AcceptedResultBuilder(session, controller)
                    .build()
            }

            override fun onPlaybackResumption(
                mediaSession: MediaSession,
                controller: MediaSession.ControllerInfo,
                isForPlayback: Boolean
            ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                val currentMediaItem = player.currentMediaItem
                if (currentMediaItem != null) {
                    return Futures.immediateFuture(
                        MediaSession.MediaItemsWithStartPosition(
                            listOf(currentMediaItem),
                            player.currentMediaItemIndex,
                            player.currentPosition
                        )
                    )
                }

                return handleResumptionFromPrefs()
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun handleResumptionFromPrefs(): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return CallbackToFutureAdapter.getFuture { completer ->
            serviceScope.launch {
                try {
                    val lastId = preferenceRepository.lastAudioId.first()
                    val audio = if (!lastId.isNullOrEmpty()) audioRepository.getAudioData(lastId) else null

                    val result = if (audio != null) {
                        val mediaItem = audio.asMediaItem(this@AudioPlaybackService)
                        MediaSession.MediaItemsWithStartPosition(listOf(mediaItem), 0, 0)
                    } else {
                        MediaSession.MediaItemsWithStartPosition(emptyList(), 0, 0)
                    }

                    completer.set(result)
                } catch (e: Exception) {
                    completer.setException(e)
                }
            }

            "AudioPlaybackResumptionTask"
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }

        super.onDestroy()
    }
}
