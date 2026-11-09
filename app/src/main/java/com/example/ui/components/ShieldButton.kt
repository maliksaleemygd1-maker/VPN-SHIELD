package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.ui.theme.VpnAccentCyan
import com.example.ui.theme.VpnConnectingYellow
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnProtectedGlow
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnTextPrimary
import com.example.ui.theme.VpnTextSecondary

@Composable
fun ShieldButton(
    status: ConnectionStatus,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = status == ConnectionStatus.CONNECTED
    val isConnecting = status == ConnectionStatus.CONNECTING

    // Infinite transitions for active pulse & cyber radar rings
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    val pulseScale by if (isConnected || isConnecting) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = if (isConnected) 1.12f else 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isConnected) 1400 else 800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Outer cyber radar expanding ripple wave
    val waveScale by if (isConnected) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.40f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_scale"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val waveAlpha by if (isConnected) {
        infiniteTransition.animateFloat(
            initialValue = 0.60f,
            targetValue = 0.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_alpha"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.0f) }
    }

    // Connecting spin rotation animation
    val connectingRotation by if (isConnecting) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "connecting_rotation"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Scale pop animation when connected
    val buttonScale by animateFloatAsState(
        targetValue = if (isConnected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    val targetColor = when (status) {
        ConnectionStatus.CONNECTED -> VpnProtectedGreen
        ConnectionStatus.CONNECTING -> VpnConnectingYellow
        ConnectionStatus.KILL_SWITCH_LOCKDOWN -> Color(0xFFFF3B30)
        ConnectionStatus.DISCONNECTED, ConnectionStatus.DISCONNECTING -> Color(0xFFFF3B30)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 350),
        label = "shield_color"
    )

    val actionButtonText = when (status) {
        ConnectionStatus.CONNECTED -> "DISCONNECT"
        ConnectionStatus.CONNECTING -> "CONNECTING..."
        ConnectionStatus.DISCONNECTING -> "DISCONNECTING..."
        ConnectionStatus.KILL_SWITCH_LOCKDOWN -> "RESET LOCKDOWN"
        ConnectionStatus.DISCONNECTED -> "CONNECT"
    }

    val subText = when (status) {
        ConnectionStatus.CONNECTED -> "Tap to Disconnect VPN"
        ConnectionStatus.CONNECTING -> "Establishing Secure Tunnel..."
        ConnectionStatus.DISCONNECTING -> "Closing Tunnel Safely"
        ConnectionStatus.KILL_SWITCH_LOCKDOWN -> "Traffic Blocked - Tap to Reset"
        ConnectionStatus.DISCONNECTED -> "Tap to Connect Secure VPN"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(220.dp)
                .testTag("shield_connect_button")
                .semantics {
                    role = Role.Button
                    contentDescription = "VPN Connection Button, current status: $actionButtonText"
                }
        ) {
            // Radar expanding ripple wave (active when connected)
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .scale(waveScale)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = VpnProtectedGreen.copy(alpha = waveAlpha),
                            shape = CircleShape
                        )
                )
            }

            // Glowing outer pulsing radial aura
            if (isConnected || isConnecting) {
                Box(
                    modifier = Modifier
                        .size(195.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isConnected) VpnProtectedGlow.copy(alpha = 0.35f) else VpnConnectingYellow.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Outer decorative ring
            Box(
                modifier = Modifier
                    .size(168.dp)
                    .clip(CircleShape)
                    .background(
                        if (isConnected) Color(0xFF07301B)
                        else if (isConnecting) Color(0xFF2B1B02)
                        else Color(0xFF26080C)
                    )
                    .border(
                        2.5.dp,
                        if (isConnected) VpnProtectedGreen
                        else if (isConnecting) VpnConnectingYellow
                        else Color(0xFFFF3B30),
                        CircleShape
                    )
            )

            // Connecting rotating border indicator
            if (isConnecting) {
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .rotate(connectingRotation)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    VpnConnectingYellow,
                                    VpnAccentCyan,
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }

            // Inner interactive touch button with spring scale
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(136.dp)
                    .scale(buttonScale)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isConnected) {
                                listOf(
                                    Color(0xFF00E5A3),
                                    Color(0xFF00A876),
                                    Color(0xFF025A38)
                                )
                            } else if (isConnecting) {
                                listOf(
                                    Color(0xFF422802),
                                    Color(0xFF211400)
                                )
                            } else {
                                listOf(
                                    Color(0xFF450D13),
                                    Color(0xFF2B070B),
                                    Color(0xFF170406)
                                )
                            }
                        )
                    )
                    .border(
                        width = if (isConnected) 3.5.dp else 3.dp,
                        brush = Brush.verticalGradient(
                            colors = if (isConnected) {
                                listOf(
                                    Color(0xFF80FFD7),
                                    VpnProtectedGreen,
                                    Color(0xFF00B377)
                                )
                            } else if (isConnecting) {
                                listOf(
                                    VpnConnectingYellow,
                                    Color(0xFFB37400)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFF5252),
                                    Color(0xFFFF3B30),
                                    Color(0xFFB31414)
                                )
                            }
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle
                    )
            ) {
                if (status == ConnectionStatus.CONNECTING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = VpnConnectingYellow,
                        strokeWidth = 3.5.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                        contentDescription = "Shield Status Icon",
                        tint = if (isConnected) Color(0xFF022E1A) else Color(0xFFFF453A),
                        modifier = Modifier
                            .size(54.dp)
                            .scale(if (isConnected) pulseScale else 1.0f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prominent Dynamic Connect / Disconnect Action Pill Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isConnected) Color(0xFF052E1B)
                    else if (isConnecting) Color(0xFF2E1C02)
                    else Color(0xFF33080E)
                )
                .border(
                    width = 1.5.dp,
                    color = if (isConnected) VpnProtectedGreen
                    else if (isConnecting) VpnConnectingYellow
                    else Color(0xFFFF3B30),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(onClick = onToggle)
                .padding(horizontal = 24.dp, vertical = 9.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.PowerSettingsNew,
                    contentDescription = actionButtonText,
                    tint = if (isConnected) VpnProtectedGreen
                    else if (isConnecting) VpnConnectingYellow
                    else Color(0xFFFF453A),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = actionButtonText,
                    color = if (isConnected) VpnProtectedGreen
                    else if (isConnecting) VpnConnectingYellow
                    else Color(0xFFFF453A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subText,
            color = if (isConnected) VpnTextPrimary else VpnTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isConnected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
