package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConnectionState
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun VpnPowerButton(
    connectionState: VpnConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) 1.22f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (connectionState == VpnConnectionState.CONNECTING || connectionState == VpnConnectionState.HANDSHAKE) 1800 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val (glowColor, buttonGradient) = when (connectionState) {
        VpnConnectionState.CONNECTED -> Pair(
            NeonGreen,
            listOf(Color(0xFF064E3B), Color(0xFF047857), NeonGreen)
        )
        VpnConnectionState.CONNECTING, VpnConnectionState.HANDSHAKE -> Pair(
            AmberAlert,
            listOf(Color(0xFF78350F), Color(0xFFD97706), AmberAlert)
        )
        VpnConnectionState.DISCONNECTING -> Pair(
            AmberAlert,
            listOf(Color(0xFF451A03), Color(0xFF78350F))
        )
        VpnConnectionState.ERROR -> Pair(
            CrimsonError,
            listOf(Color(0xFF7F1D1D), CrimsonError)
        )
        VpnConnectionState.DISCONNECTED -> Pair(
            ElectricCyan.copy(alpha = 0.4f),
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
        )
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .testTag("vpn_power_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing energy rings
        Canvas(modifier = Modifier.size(200.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width / 2f - 18.dp.toPx()

            if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) {
                drawCircle(
                    color = glowColor.copy(alpha = 0.15f),
                    radius = baseRadius * pulseScale,
                    center = center
                )
                drawCircle(
                    color = glowColor.copy(alpha = 0.28f),
                    radius = baseRadius * ((pulseScale - 1f) * 0.5f + 1f),
                    center = center
                )
            }

            // Outer tech ring track
            drawCircle(
                color = CardBorder,
                radius = baseRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Animated rotating neon perimeter segments
            val sweep = if (connectionState == VpnConnectionState.CONNECTING) 120f else 60f
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        glowColor.copy(alpha = 0.1f),
                        glowColor,
                        glowColor.copy(alpha = 0.1f)
                    )
                ),
                startAngle = rotationAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        glowColor.copy(alpha = 0.1f),
                        glowColor,
                        glowColor.copy(alpha = 0.1f)
                    )
                ),
                startAngle = rotationAngle + 180f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Central Power Button
        Box(
            modifier = Modifier
                .size(136.dp)
                .shadow(
                    elevation = if (connectionState == VpnConnectionState.CONNECTED) 24.dp else 12.dp,
                    shape = CircleShape,
                    ambientColor = glowColor,
                    spotColor = glowColor
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = buttonGradient,
                        center = Offset.Unspecified,
                        radius = 180f
                    )
                )
                .border(
                    width = 2.dp,
                    color = glowColor.copy(alpha = 0.8f),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = glowColor),
                    onClick = onClick
                )
                .testTag("vpn_power_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = when (connectionState) {
                        VpnConnectionState.CONNECTED -> Icons.Default.Shield
                        VpnConnectionState.CONNECTING, VpnConnectionState.HANDSHAKE -> Icons.Default.Lock
                        VpnConnectionState.DISCONNECTED -> Icons.Default.PowerSettingsNew
                        else -> Icons.Default.LockOpen
                    },
                    contentDescription = "VPN Power Button",
                    tint = if (connectionState == VpnConnectionState.CONNECTED) TextWhite else ElectricCyan,
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> "SECURED"
                        VpnConnectionState.CONNECTING -> "LINKING"
                        VpnConnectionState.HANDSHAKE -> "CIPHER"
                        VpnConnectionState.DISCONNECTING -> "CLOSING"
                        VpnConnectionState.ERROR -> "RETRY"
                        VpnConnectionState.DISCONNECTED -> "CONNECT"
                    },
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
