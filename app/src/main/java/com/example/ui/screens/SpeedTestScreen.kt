package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpeedTestPhase
import com.example.model.VpnServer
import com.example.ui.VpnViewModel
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedTestScreen(
    viewModel: VpnViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speedTestState by viewModel.speedTestState.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()

    val targetServer = speedTestState.targetServer ?: activeServer ?: selectedServer

    val isRunning = speedTestState.phase != SpeedTestPhase.IDLE && speedTestState.phase != SpeedTestPhase.COMPLETED

    val animatedSpeed by animateFloatAsState(
        targetValue = speedTestState.currentSpeedMbps,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "gauge_speed"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianNavy)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("speed_test_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Speed & Latency Benchmark",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Low Latency & High Throughput Verification",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Server Selector & Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = targetServer.flagEmoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Testing Server: ${targetServer.city}",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${targetServer.stateOrRegion} • ${targetServer.tag}",
                            color = ElectricCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${targetServer.latencyMs} ms",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High-Tech Animated Speedometer Gauge
        Box(
            modifier = Modifier
                .size(240.dp)
                .testTag("speed_gauge_container"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val strokeWidth = 14.dp.toPx()
                val radius = (size.width - strokeWidth) / 2f

                // Track Background Arc (135 degrees to 405 degrees -> 270 degree arc)
                drawArc(
                    color = CardBorder,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Gradient Progress Arc
                val maxSpeed = 120f
                val sweepRatio = (animatedSpeed / maxSpeed).coerceIn(0.02f, 1f)
                val activeSweep = 270f * sweepRatio

                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(ElectricCyan, BrightCyan, NeonGreen)
                    ),
                    startAngle = 135f,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Central Digital Speed Readout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (speedTestState.phase) {
                        SpeedTestPhase.IDLE -> "0.0"
                        SpeedTestPhase.PINGING -> "PING"
                        SpeedTestPhase.DOWNLOADING, SpeedTestPhase.UPLOADING -> String.format(Locale.US, "%.1f", animatedSpeed)
                        SpeedTestPhase.COMPLETED -> String.format(Locale.US, "%.1f", speedTestState.downloadSpeedMbps)
                    },
                    color = TextWhite,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = when (speedTestState.phase) {
                        SpeedTestPhase.PINGING -> "${speedTestState.pingMs} ms"
                        else -> "Mbps"
                    },
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (speedTestState.phase) {
                        SpeedTestPhase.IDLE -> "READY"
                        SpeedTestPhase.PINGING -> "LATENCY TEST"
                        SpeedTestPhase.DOWNLOADING -> "TESTING DOWNLOAD"
                        SpeedTestPhase.UPLOADING -> "TESTING UPLOAD"
                        SpeedTestPhase.COMPLETED -> "BENCHMARK COMPLETE"
                    },
                    color = if (speedTestState.phase == SpeedTestPhase.COMPLETED) NeonGreen else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Benchmark Results Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Latency / Ping
            BenchmarkStatCard(
                label = "PING / LATENCY",
                value = if (speedTestState.pingMs > 0) "${speedTestState.pingMs} ms" else "--",
                subtext = if (speedTestState.jitterMs > 0) "${speedTestState.jitterMs}ms Jitter" else "Low Jitter",
                accentColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )

            // Download
            BenchmarkStatCard(
                label = "DOWNLOAD",
                value = if (speedTestState.downloadSpeedMbps > 0) String.format(Locale.US, "%.1f", speedTestState.downloadSpeedMbps) else "--",
                subtext = "Mbps Throughput",
                accentColor = ElectricCyan,
                modifier = Modifier.weight(1f)
            )

            // Upload
            BenchmarkStatCard(
                label = "UPLOAD",
                value = if (speedTestState.uploadSpeedMbps > 0) String.format(Locale.US, "%.1f", speedTestState.uploadSpeedMbps) else "--",
                subtext = "Mbps Upstream",
                accentColor = BrightCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stability & Browsing Quality Analysis
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Stability",
                        tint = NeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BROWSING STABILITY ANALYSIS",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (speedTestState.phase == SpeedTestPhase.COMPLETED) {
                        "Your connection through ${targetServer.city} exhibits optimal low-latency routing (${targetServer.latencyMs}ms). 4K HDR streaming, zero buffer web browsing, and secure banking will execute without lag or packet drops."
                    } else {
                        "Run the benchmark to measure round-trip packet latency, download throughput, and jitter to ${targetServer.city} (${targetServer.tag})."
                    },
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Start Benchmark Button
        Button(
            onClick = {
                viewModel.runSpeedTest(targetServer)
            },
            enabled = !isRunning,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("run_speed_test_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricCyan,
                disabledContainerColor = DarkSurfaceVariant
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start Test",
                tint = if (isRunning) TextMuted else ObsidianNavy,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRunning) "Benchmarking Latency & Bandwidth..." else "Start Latency & Speed Test",
                color = if (isRunning) TextMuted else ObsidianNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BenchmarkStatCard(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtext, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
