package com.nndwn.whitenoise.service

import com.nndwn.whitenoise.IoDispatcher
import com.nndwn.whitenoise.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class FocusTimerManager @Inject constructor(
    private val playbackManager: AudioPlaybackManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val _timerRemainingSeconds = MutableStateFlow<Long?>(null)
    val timerRemainingSeconds = _timerRemainingSeconds.asStateFlow()

    private var countdownJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)

    fun setFocusTimer(timerTime: TimerTime) {
        countdownJob?.cancel()
        if (timerTime == TimerTime.OFF) {
            _timerRemainingSeconds.value = null
            return
        }

        _timerRemainingSeconds.value = when {
            timerTime.hour < 0 -> {
                abs(timerTime.hour).toLong()
            }
            else -> {
                timerTime.hour * 3600L
            }
        }

        countdownJob = scope.launch(ioDispatcher) {
            while (isActive && (_timerRemainingSeconds.value ?: 0L) > 0L) {
                delay(1000.milliseconds)
                _timerRemainingSeconds.value = (_timerRemainingSeconds.value ?: 1L) - 1
            }
            
            if (_timerRemainingSeconds.value == 0L) {
                withContext(mainDispatcher) {
                    playbackManager.mediaController?.pause()
                }
                _timerRemainingSeconds.value = null
            }
        }
    }
}
