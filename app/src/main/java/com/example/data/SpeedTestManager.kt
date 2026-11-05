package com.example.data

import com.example.model.VpnServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class SpeedTestPhase {
    IDLE,
    PINGING,
    DOWNLOADING,
    UPLOADING,
    COMPLETED
}

data class SpeedTestState(
    val phase: SpeedTestPhase = SpeedTestPhase.IDLE,
    val targetServer: VpnServer? = null,
    val pingMs: Int = 0,
    val jitterMs: Int = 0,
    val currentSpeedMbps: Float = 0f,
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val progress: Float = 0f
)

class SpeedTestManager {

    private val _testState = MutableStateFlow(SpeedTestState())
    val testState: StateFlow<SpeedTestState> = _testState.asStateFlow()

    suspend fun runSpeedTest(server: VpnServer) {
        _testState.value = SpeedTestState(
            phase = SpeedTestPhase.PINGING,
            targetServer = server,
            pingMs = server.latencyMs,
            jitterMs = 1,
            progress = 0.1f
        )

        // Ping test phase
        for (i in 1..4) {
            delay(200)
            val currentPing = (server.latencyMs + Random.nextInt(-2, 3)).coerceAtLeast(8)
            val currentJitter = Random.nextInt(1, 3)
            _testState.value = _testState.value.copy(
                pingMs = currentPing,
                jitterMs = currentJitter,
                progress = 0.1f + (i * 0.05f)
            )
        }

        // Download test phase
        _testState.value = _testState.value.copy(phase = SpeedTestPhase.DOWNLOADING)
        val peakDl = when {
            server.latencyMs < 20 -> Random.nextFloat() * 20f + 85f // 85 - 105 Mbps for Baltimore / Ashburn
            server.latencyMs < 30 -> Random.nextFloat() * 18f + 72f // 72 - 90 Mbps for Bluffdale / Silicon Valley
            else -> Random.nextFloat() * 15f + 55f
        }

        for (step in 1..10) {
            delay(250)
            val currentSpeed = (peakDl * (step / 10f) * (0.85f + Random.nextFloat() * 0.2f)).coerceIn(10f, peakDl)
            _testState.value = _testState.value.copy(
                currentSpeedMbps = currentSpeed,
                downloadSpeedMbps = currentSpeed,
                progress = 0.3f + (step * 0.035f)
            )
        }

        val finalDl = peakDl

        // Upload test phase
        _testState.value = _testState.value.copy(
            phase = SpeedTestPhase.UPLOADING,
            downloadSpeedMbps = finalDl
        )
        val peakUl = finalDl * (0.35f + Random.nextFloat() * 0.1f) // ~30-45 Mbps upload

        for (step in 1..10) {
            delay(250)
            val currentSpeed = (peakUl * (step / 10f) * (0.85f + Random.nextFloat() * 0.2f)).coerceIn(5f, peakUl)
            _testState.value = _testState.value.copy(
                currentSpeedMbps = currentSpeed,
                uploadSpeedMbps = currentSpeed,
                progress = 0.65f + (step * 0.035f)
            )
        }

        _testState.value = _testState.value.copy(
            phase = SpeedTestPhase.COMPLETED,
            currentSpeedMbps = 0f,
            downloadSpeedMbps = finalDl,
            uploadSpeedMbps = peakUl,
            progress = 1.0f
        )
    }

    fun reset() {
        _testState.value = SpeedTestState()
    }
}
