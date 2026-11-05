package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConnectionState
import com.example.model.VpnServer
import com.example.ui.VpnViewModel
import com.example.ui.components.VpnPowerButton
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.util.Locale

@Composable
fun ConnectScreen(
    viewModel: VpnViewModel,
    onNavigateToServers: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSpeedTest: () -> Unit,
    onPrepareRequired: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectionState by viewModel.connectionState.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val sessionStats by viewModel.sessionStats.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val displayServer = if (connectionState == VpnConnectionState.CONNECTED) (activeServer ?: selectedServer) else selectedServer

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianNavy)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Brand Header & Status Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(ElectricCyan, BrightCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "CipherVPN Logo",
                        tint = ObsidianNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CipherVPN",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Zero-Logs • Quantum Safe",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Security Status Badge
            val statusColor = when (connectionState) {
                VpnConnectionState.CONNECTED -> NeonGreen
                VpnConnectionState.CONNECTING, VpnConnectionState.HANDSHAKE -> AmberAlert
                else -> CrimsonError
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> "PROTECTED"
                        VpnConnectionState.CONNECTING, VpnConnectionState.HANDSHAKE -> "LINKING..."
                        else -> "UNPROTECTED"
                    },
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // USA Priority Server Quick Selectors: Baltimore & Bluffdale & Fastest
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "USA PRIORITY LOCATIONS",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ultra-Low Latency",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Baltimore Quick Button
                QuickServerPill(
                    title = "Baltimore, MD",
                    sub = "18 ms • East Hub",
                    isSelected = displayServer.id == "us_baltimore",
                    onClick = {
                        viewModel.selectServer(
                            VpnServer(
                                id = "us_baltimore",
                                city = "Baltimore",
                                stateOrRegion = "Maryland",
                                country = "United States",
                                countryCode = "US",
                                flagEmoji = "🇺🇸",
                                ipAddress = "104.28.19.42",
                                latencyMs = 18,
                                loadPercentage = 24,
                                isVirtual = false,
                                isFeatured = true,
                                tag = "Low Latency Hub"
                            ),
                            context
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                // Bluffdale Quick Button
                QuickServerPill(
                    title = "Bluffdale, UT",
                    sub = "22 ms • Vault",
                    isSelected = displayServer.id == "us_bluffdale",
                    onClick = {
                        viewModel.selectServer(
                            VpnServer(
                                id = "us_bluffdale",
                                city = "Bluffdale",
                                stateOrRegion = "Utah",
                                country = "United States",
                                countryCode = "US",
                                flagEmoji = "🇺🇸",
                                ipAddress = "198.51.100.77",
                                latencyMs = 22,
                                loadPercentage = 19,
                                isVirtual = false,
                                isFeatured = true,
                                tag = "Quantum Vault"
                            ),
                            context
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                // Quick Connect Fastest Button
                QuickServerPill(
                    title = "Best Server",
                    sub = "Auto-Pick",
                    isSelected = false,
                    onClick = {
                        viewModel.quickConnectFastest(context, onPrepareRequired)
                    },
                    modifier = Modifier.weight(0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Big Interactive Animated Power Button
        VpnPowerButton(
            connectionState = connectionState,
            onClick = {
                viewModel.toggleVpnConnection(context, onPrepareRequired)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Connection State Heading & Subtext
        Text(
            text = when (connectionState) {
                VpnConnectionState.CONNECTED -> "SECURE TUNNEL ENGAGED"
                VpnConnectionState.CONNECTING -> "NEGOTIATING ENCRYPTION..."
                VpnConnectionState.HANDSHAKE -> "CRYPTOGRAPHIC HANDSHAKE"
                VpnConnectionState.DISCONNECTING -> "DISCONNECTING TUNNEL..."
                VpnConnectionState.ERROR -> "CONNECTION FAILED"
                VpnConnectionState.DISCONNECTED -> "VPN IS DISCONNECTED"
            },
            color = when (connectionState) {
                VpnConnectionState.CONNECTED -> NeonGreen
                VpnConnectionState.CONNECTING, VpnConnectionState.HANDSHAKE -> AmberAlert
                VpnConnectionState.ERROR -> CrimsonError
                else -> TextWhite
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = when (connectionState) {
                VpnConnectionState.CONNECTED -> "Virtual IP: ${sessionStats.virtualIp} • 0% Logs"
                VpnConnectionState.CONNECTING -> "Establishing ${settings.selectedProtocol.displayName} session"
                VpnConnectionState.HANDSHAKE -> "Exchanging ${settings.selectedProtocol.keyExchange} keys"
                else -> "Tap the power button to encrypt all web traffic"
            },
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Selected Server Card with Navigation to Server List
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToServers() }
                .testTag("selected_server_card"),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = displayServer.flagEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${displayServer.city}, ${displayServer.stateOrRegion}",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (displayServer.isVirtual) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VIRTUAL",
                                color = ElectricCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${displayServer.tag} • ${displayServer.bandwidthCapacityGbps} Gbps Core",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${displayServer.latencyMs} ms",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Change Server",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Real-Time Telemetry & Performance Dashboard (Visible when connected)
        AnimatedVisibility(
            visible = connectionState == VpnConnectionState.CONNECTED,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Live Speed & Latency Metrics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Live Telemetry",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE NETWORK SPEED & STABILITY",
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Text(
                                text = "Stable • Low Jitter",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Download & Upload Rates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Download
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Download",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "DOWNLOAD",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1f", sessionStats.downloadSpeedMbps),
                                    color = TextWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Mbps",
                                    color = ElectricCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Upload
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Upload",
                                        tint = BrightCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "UPLOAD",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1f", sessionStats.uploadSpeedMbps),
                                    color = TextWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Mbps",
                                    color = BrightCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Ping & Latency
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Ping",
                                        tint = NeonGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LATENCY",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${sessionStats.currentPingMs}",
                                    color = TextWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "ms (${sessionStats.jitterMs}ms jitter)",
                                    color = NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Session Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Duration
                            val hours = sessionStats.durationSeconds / 3600
                            val minutes = (sessionStats.durationSeconds % 3600) / 60
                            val seconds = sessionStats.durationSeconds % 60
                            val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Duration",
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Duration: $timeFormatted",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            // Total Data
                            val totalMb = (sessionStats.totalBytesReceived + sessionStats.totalBytesSent) / (1024f * 1024f)
                            Text(
                                text = String.format(Locale.US, "Data: %.1f MB", totalMb),
                                color = TextMuted,
                                fontSize = 11.sp
                            )

                            // Protocol Badge
                            Text(
                                text = settings.selectedProtocol.displayName,
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security Shield Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ACTIVE PRIVACY ENCRYPTION",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SecurityPill(
                                label = "Protocol",
                                value = settings.selectedProtocol.displayName,
                                icon = Icons.Default.VpnKey
                            )
                            SecurityPill(
                                label = "Cipher",
                                value = settings.selectedProtocol.cipherDescription.split(" ").first(),
                                icon = Icons.Default.Lock
                            )
                            SecurityPill(
                                label = "DNS Leak",
                                value = "Protected",
                                icon = Icons.Default.Security
                            )
                            SecurityPill(
                                label = "Kill Switch",
                                value = if (settings.killSwitchEnabled) "Active" else "Off",
                                icon = Icons.Default.Check
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row: Speed Test & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigateToSpeedTest,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("speed_test_button"),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Test Latency & Speed",
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Speed & Ping Test",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("vpn_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "VPN Settings",
                    tint = NeonGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VPN Settings",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun QuickServerPill(
    title: String,
    sub: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkSurfaceVariant else CardBackground)
            .border(
                1.dp,
                if (isSelected) ElectricCyan else CardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                color = if (isSelected) ElectricCyan else TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                color = if (isSelected) NeonGreen else TextMuted,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SecurityPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ElectricCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = TextMuted, fontSize = 9.sp)
        Text(text = value, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
