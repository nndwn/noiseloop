package com.nndwn.whitenoise.service

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class AudioPlaybackManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: AudioRepository
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private val _sessionTrackDuration = MutableStateFlow(0L)
    val sessionTrackDuration = _sessionTrackDuration.asStateFlow()
    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem = _currentMediaItem.asStateFlow()
    private val _playbackError = MutableSharedFlow<Boolean>()
    val playbackError = _playbackError.asSharedFlow()

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    var mediaController: MediaController? = null
        private set

    private var baseAccumulatedTime = 0L
    private var lastPlayStartTime = 0L
    private var tickerJob: Job? = null
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeAudio: StateFlow<DataAudio?> = _currentMediaItem
        .map { mediaItem -> mediaItem?.mediaId }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getAudioFlowById(id)
        }
        .stateIn(
            scope = managerScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    init {
        initializeController()
    }

    private fun initializeController(){
        val sessionToken =
            SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            val controller = mediaControllerFuture?.get()
            mediaController = controller

            controller?.let {
                _isPlaying.value = it.isPlaying
                _currentMediaItem.value = it.currentMediaItem
                if (it.isPlaying) startTrackingTime()
            }

            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) {
                        lastPlayStartTime = System.currentTimeMillis()
                        startTrackingTime()
                    } else {
                        stopTrackingTime()
                        if (lastPlayStartTime != 0L) {
                            baseAccumulatedTime += System.currentTimeMillis() - lastPlayStartTime
                            lastPlayStartTime = 0L
                        }
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _currentMediaItem.value = mediaItem

                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    managerScope.launch {
                        _playbackError.emit(true)
                    }
                    stopTrackingTime()
                }
            })
        }, ContextCompat.getMainExecutor(context))
    }

    fun play(mediaItem: MediaItem) {
        stopTrackingTime()
        baseAccumulatedTime = 0L
        lastPlayStartTime = System.currentTimeMillis()
        _sessionTrackDuration.value = 0L

        mediaController?.let { player ->
            player.setMediaItem(mediaItem)
            player.prepare()
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.play()
        }
        startTrackingTime()
        _currentMediaItem.value = mediaItem
    }

    fun togglePlayPause() {
        mediaController?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }


    private fun startTrackingTime() {
        tickerJob?.cancel()
        if (lastPlayStartTime == 0L) lastPlayStartTime = System.currentTimeMillis()
        tickerJob = managerScope.launch {
            while (isActive) {
                val elapsedTimeThisSession = System.currentTimeMillis() - lastPlayStartTime
                _sessionTrackDuration.value = baseAccumulatedTime + elapsedTimeThisSession
                delay(1000.milliseconds)
            }
        }
    }

    private fun stopTrackingTime() {
        tickerJob?.cancel()
    }

    fun release() {
        stopTrackingTime()
        mediaControllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null

    }

}