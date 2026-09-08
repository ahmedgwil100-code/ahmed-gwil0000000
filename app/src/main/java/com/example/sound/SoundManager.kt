package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.PI
import kotlin.math.sin

class SoundManager {
    var isSoundEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.Default)
    private val audioMutex = Mutex()

    private val sampleRate = 22050
    private var activeTrack: AudioTrack? = null

    // Precomputed sound buffers for zero-latency instant playback
    private val tapBuffer by lazy { generateTapSound() }
    private val correctChimeBuffer by lazy { generateChimeSound() }
    private val wrongBuzzBuffer by lazy { generateBuzzSound() }
    private val victoryFanfareBuffer by lazy { generateFanfareSound() }
    private val tickBuffer by lazy { generateTickSound() }
    private val swooshBuffer by lazy { generateSwooshSound() }
    private val coinSparkleBuffer by lazy { generateCoinSound() }
    private val gameOverBuffer by lazy { generateGameOverSound() }

    fun playTap() {
        if (!isSoundEnabled) return
        playBuffer(tapBuffer)
    }

    fun playCorrectWord() {
        if (!isSoundEnabled) return
        playBuffer(correctChimeBuffer)
    }

    fun playWrong() {
        if (!isSoundEnabled) return
        playBuffer(wrongBuzzBuffer)
    }

    fun playVictory() {
        if (!isSoundEnabled) return
        playBuffer(victoryFanfareBuffer)
    }

    fun playTick() {
        if (!isSoundEnabled) return
        playBuffer(tickBuffer)
    }

    fun playSwoosh() {
        if (!isSoundEnabled) return
        playBuffer(swooshBuffer)
    }

    fun playBonusWord() {
        if (!isSoundEnabled) return
        playBuffer(coinSparkleBuffer)
    }

    fun playGameOver() {
        if (!isSoundEnabled) return
        playBuffer(gameOverBuffer)
    }

    private fun playBuffer(buffer: ShortArray) {
        scope.launch {
            audioMutex.withLock {
                try {
                    // Stop and release any previous track cleanly
                    activeTrack?.let {
                        try {
                            if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                                it.stop()
                            }
                            it.release()
                        } catch (_: Throwable) {
                        }
                    }
                    activeTrack = null

                    val track = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(buffer.size * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()

                    track.write(buffer, 0, buffer.size)
                    track.play()
                    activeTrack = track
                } catch (_: Throwable) {
                    // Ignore audio hardware/resource allocation failures gracefully
                }
            }
        }
    }

    fun release() {
        scope.launch {
            audioMutex.withLock {
                try {
                    activeTrack?.release()
                    activeTrack = null
                } catch (_: Throwable) {
                }
            }
        }
    }

    private fun generateTapSound(): ShortArray {
        val duration = 0.03 // 30ms
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = 1.0 - (i.toDouble() / totalSamples)
            val freq = 600.0 - (t * 5000.0).coerceAtMost(300.0)
            val sample = sin(2 * PI * freq * t) * env * 0.4
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateChimeSound(): ShortArray {
        val duration = 0.35 // 350ms bright chime
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        // Arpeggio notes: E6 (1318 Hz) then G#6 (1661 Hz) then B6 (1975 Hz)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = (1.0 - (i.toDouble() / totalSamples)).coerceAtLeast(0.0)
            val decay = env * env

            val note1 = sin(2 * PI * 1318.5 * t) * if (t < 0.25) 0.3 else 0.0
            val note2 = sin(2 * PI * 1661.2 * (t - 0.06).coerceAtLeast(0.0)) * if (t >= 0.06) 0.35 else 0.0
            val note3 = sin(2 * PI * 2093.0 * (t - 0.12).coerceAtLeast(0.0)) * if (t >= 0.12) 0.4 else 0.0

            val sample = (note1 + note2 + note3) * decay
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateBuzzSound(): ShortArray {
        val duration = 0.18 // 180ms gentle buzz
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = 1.0 - (i.toDouble() / totalSamples)
            val wave = sin(2 * PI * 130.0 * t) * 0.4 + sin(2 * PI * 260.0 * t) * 0.2
            val sample = wave * env
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateFanfareSound(): ShortArray {
        val duration = 0.8 // 800ms victory fanfare
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        // Notes: C5 (523), E5 (659), G5 (784), C6 (1046)
        val notes = listOf(523.25 to 0.0, 659.25 to 0.15, 784.0 to 0.30, 1046.5 to 0.45)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            var sum = 0.0
            for ((freq, startTime) in notes) {
                if (t >= startTime) {
                    val noteT = t - startTime
                    val noteDur = if (freq > 1000) 0.35 else 0.15
                    val noteEnv = (1.0 - (noteT / noteDur)).coerceIn(0.0, 1.0)
                    sum += sin(2 * PI * freq * noteT) * noteEnv * 0.3
                }
            }
            buffer[i] = (sum.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateTickSound(): ShortArray {
        val duration = 0.02 // 20ms subtle tick
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = 1.0 - (i.toDouble() / totalSamples)
            val sample = sin(2 * PI * 1200.0 * t) * env * 0.25
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateSwooshSound(): ShortArray {
        val duration = 0.08 // 80ms swoosh
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = sin(PI * (i.toDouble() / totalSamples))
            val freq = 400.0 + (t * 800.0)
            val sample = sin(2 * PI * freq * t) * env * 0.2
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateCoinSound(): ShortArray {
        val duration = 0.3 // 300ms sparkle
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        // High sparkle: B6 (1975 Hz) and E7 (2637 Hz)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env1 = (1.0 - (t / 0.15)).coerceIn(0.0, 1.0)
            val env2 = if (t >= 0.08) (1.0 - ((t - 0.08) / 0.22)).coerceIn(0.0, 1.0) else 0.0
            val s1 = sin(2 * PI * 1975.5 * t) * env1 * 0.35
            val s2 = sin(2 * PI * 2637.0 * (t - 0.08)) * env2 * 0.4
            buffer[i] = ((s1 + s2).coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateGameOverSound(): ShortArray {
        val duration = 0.6 // 600ms descending tone
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = (1.0 - (i.toDouble() / totalSamples)).coerceIn(0.0, 1.0)
            val freq = 440.0 - (t * 300.0).coerceAtMost(240.0)
            val sample = sin(2 * PI * freq * t) * env * 0.35
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
