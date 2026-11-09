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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ServerEntity
import com.example.data.model.ConnectionStatus
import com.example.ui.theme.VpnAccentCyan
import com.example.ui.theme.VpnAccentPurple
import com.example.ui.theme.VpnBackgroundDark
import com.example.ui.theme.VpnConnectingYellow
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnSurfaceDark
import com.example.ui.theme.VpnTextMuted
import com.example.ui.theme.VpnTextPrimary
import com.example.ui.theme.VpnTextSecondary
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VpnWorldMap(
    servers: List<ServerEntity>,
    selectedServer: ServerEntity?,
    connectionStatus: ConnectionStatus,
    onServerSelect: (ServerEntity) -> Unit,
    modifier: Modifier = Modifier,
    heightDp: Int = 220,
    showHud: Boolean = true
) {
    // Pulse animation for active target beacon
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseProgress"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleProgress"
    )

    // Pre-calculated representative continent land points for the world mesh grid
    val worldLandPoints = remember { generateWorldGridPoints() }
    var cachedSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    var cachedLandPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var cachedServerOffsets by remember { mutableStateOf<List<Pair<ServerEntity, Offset>>>(emptyList()) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF090D16))
            .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(16.dp))
            .testTag("vpn_world_map_container")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(servers) {
                    detectTapGestures { tapOffset ->
                        // Find closest server within touch tolerance (36dp in px)
                        val touchRadius = 40f * density
                        var closest: ServerEntity? = null
                        var minDistance = Float.MAX_VALUE

                        for (server in servers) {
                            val pos = projectCoord(server.latitude, server.longitude, size.width.toFloat(), size.height.toFloat())
                            val dx = pos.x - tapOffset.x
                            val dy = pos.y - tapOffset.y
                            val dist = sqrt(dx * dx + dy * dy)
                            if (dist < touchRadius && dist < minDistance) {
                                minDistance = dist
                                closest = server
                            }
                        }

                        closest?.let { onServerSelect(it) }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Cache coordinate projections when dimensions change to avoid re-projecting 470 points at 120 FPS
            if (cachedSize.width != canvasW || cachedSize.height != canvasH) {
                cachedSize = size
                cachedLandPoints = worldLandPoints.map { projectCoord(it.first, it.second, canvasW, canvasH) }
                cachedServerOffsets = servers.map { it to projectCoord(it.latitude, it.longitude, canvasW, canvasH) }
            }

            // 1. Draw subtle tactical cyber latitude/longitude coordinate grid
            drawTacticalGrid(canvasW, canvasH)

            // 2. Draw continent point cloud dots in ONE single hardware-accelerated batch call
            if (cachedLandPoints.isNotEmpty()) {
                drawPoints(
                    points = cachedLandPoints,
                    pointMode = PointMode.Points,
                    color = Color(0xFF1E293B),
                    strokeWidth = 2.8f,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw ambient VPN server nodes across the world without trigonometric re-calculation
            val ambientColor = VpnAccentCyan.copy(alpha = 0.5f)
            for ((srv, pos) in cachedServerOffsets) {
                if (srv.id != selectedServer?.id) {
                    drawCircle(
                        color = ambientColor,
                        radius = 2.8f,
                        center = pos
                    )
                }
            }

            // 4. Draw Active/Selected Server Node with Radar & Beacons
            selectedServer?.let { active ->
                val activePos = cachedServerOffsets.find { it.first.id == active.id }?.second
                    ?: projectCoord(active.latitude, active.longitude, canvasW, canvasH)
                val isConnected = connectionStatus == ConnectionStatus.CONNECTED
                val isConnecting = connectionStatus == ConnectionStatus.CONNECTING

                val beaconColor = when {
                    isConnected -> VpnProtectedGreen
                    isConnecting -> VpnConnectingYellow
                    else -> VpnAccentCyan
                }

                // Origin: default local user location (e.g., representative gateway at central coordinates)
                val originPos = Offset(canvasW * 0.48f, canvasH * 0.40f)

                // If connecting or connected, draw dynamic curved trajectory line
                if (isConnected || isConnecting) {
                    val trajectoryPath = Path().apply {
                        moveTo(originPos.x, originPos.y)
                        val controlX = (originPos.x + activePos.x) / 2f
                        val controlY = kotlin.math.min(originPos.y, activePos.y) - (canvasH * 0.22f)
                        quadraticTo(controlX, controlY, activePos.x, activePos.y)
                    }

                    // Cyber dashed curved beam
                    drawPath(
                        path = trajectoryPath,
                        color = beaconColor.copy(alpha = 0.45f),
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // Animated streaming data packet particle along trajectory
                    val t = particleProgress
                    val controlX = (originPos.x + activePos.x) / 2f
                    val controlY = kotlin.math.min(originPos.y, activePos.y) - (canvasH * 0.22f)
                    val px = (1 - t) * (1 - t) * originPos.x + 2 * (1 - t) * t * controlX + t * t * activePos.x
                    val py = (1 - t) * (1 - t) * originPos.y + 2 * (1 - t) * t * controlY + t * t * activePos.y

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, beaconColor, Color.Transparent),
                            center = Offset(px, py),
                            radius = 8f
                        ),
                        radius = 6f,
                        center = Offset(px, py)
                    )
                }

                // Expanding radar pulse rings
                val maxRadius = 42f
                val ringRadius1 = pulseProgress * maxRadius
                val ringAlpha1 = (1f - pulseProgress).coerceIn(0f, 1f)
                drawCircle(
                    color = beaconColor.copy(alpha = ringAlpha1 * 0.8f),
                    radius = ringRadius1,
                    center = activePos,
                    style = Stroke(width = 1.8f)
                )

                val pulseProgress2 = (pulseProgress + 0.5f) % 1f
                val ringRadius2 = pulseProgress2 * maxRadius
                val ringAlpha2 = (1f - pulseProgress2).coerceIn(0f, 1f)
                drawCircle(
                    color = beaconColor.copy(alpha = ringAlpha2 * 0.5f),
                    radius = ringRadius2,
                    center = activePos,
                    style = Stroke(width = 1.4f)
                )

                // Radar scanning line
                val rad = Math.toRadians(sweepAngle.toDouble())
                val sweepTarget = Offset(
                    (activePos.x + cos(rad) * 28f).toFloat(),
                    (activePos.y + sin(rad) * 28f).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(beaconColor.copy(alpha = 0.7f), Color.Transparent),
                        start = activePos,
                        end = sweepTarget
                    ),
                    start = activePos,
                    end = sweepTarget,
                    strokeWidth = 1.6f
                )

                // Tactical crosshair ticks around the target node
                val crossSize = 10f
                val gap = 5f
                drawLine(beaconColor.copy(alpha = 0.8f), Offset(activePos.x - crossSize, activePos.y), Offset(activePos.x - gap, activePos.y), 1.5f)
                drawLine(beaconColor.copy(alpha = 0.8f), Offset(activePos.x + gap, activePos.y), Offset(activePos.x + crossSize, activePos.y), 1.5f)
                drawLine(beaconColor.copy(alpha = 0.8f), Offset(activePos.x, activePos.y - crossSize), Offset(activePos.x, activePos.y - gap), 1.5f)
                drawLine(beaconColor.copy(alpha = 0.8f), Offset(activePos.x, activePos.y + gap), Offset(activePos.x, activePos.y + crossSize), 1.5f)

                // Solid center pinpoint with white core
                drawCircle(
                    color = beaconColor,
                    radius = 4.5f,
                    center = activePos
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.2f,
                    center = activePos
                )
            }
        }

        // Top Status HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (connectionStatus) {
                                ConnectionStatus.CONNECTED -> VpnProtectedGreen
                                ConnectionStatus.CONNECTING -> VpnConnectingYellow
                                else -> VpnAccentCyan
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (connectionStatus == ConnectionStatus.CONNECTED) "LIVE MAP: ENCRYPTED" else "GLOBAL WIREGUARD MESH (80+ NODES)",
                    color = VpnTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(VpnSurfaceCard.copy(alpha = 0.85f))
                    .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Radar",
                        tint = VpnAccentCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "10 Gbps BBR",
                        color = VpnAccentCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom HUD Card displaying active country details & live coordinates
        if (showHud && selectedServer != null) {
            val server = selectedServer
            val latFormatted = String.format(java.util.Locale.US, "%.2f° %s", kotlin.math.abs(server.latitude), if (server.latitude >= 0) "N" else "S")
            val lonFormatted = String.format(java.util.Locale.US, "%.2f° %s", kotlin.math.abs(server.longitude), if (server.longitude >= 0) "E" else "W")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VpnSurfaceDark.copy(alpha = 0.92f))
                    .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.flagEmoji,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${server.cityName}, ${server.countryName}",
                                    color = VpnTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${server.latencyMs}ms",
                                    color = if (server.latencyMs < 20) VpnProtectedGreen else VpnConnectingYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Coordinates: $latFormatted, $lonFormatted | IP: ${server.endpointIp}",
                                color = VpnTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (connectionStatus == ConnectionStatus.CONNECTED)
                                    VpnProtectedGreen.copy(alpha = 0.2f)
                                else
                                    VpnAccentCyan.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (connectionStatus == ConnectionStatus.CONNECTED) "ACTIVE" else "READY",
                            color = if (connectionStatus == ConnectionStatus.CONNECTED) VpnProtectedGreen else VpnAccentCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawTacticalGrid(width: Float, height: Float) {
    val gridColor = Color(0xFF131B2C)
    // Longitude lines
    for (i in 1..7) {
        val x = width * (i / 8f)
        drawLine(gridColor, Offset(x, 0f), Offset(x, height), 0.8f)
    }
    // Latitude lines
    for (j in 1..3) {
        val y = height * (j / 4f)
        drawLine(gridColor, Offset(0f, y), Offset(width, y), 0.8f)
    }

    // Equator line highlighted
    val equatorY = height * 0.5f
    drawLine(
        color = Color(0xFF1E293B),
        start = Offset(0f, equatorY),
        end = Offset(width, equatorY),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
    )
}

private fun projectCoord(lat: Float, lon: Float, width: Float, height: Float): Offset {
    // Equirectangular projection with margin padding
    val x = ((lon + 180f) / 360f) * width
    val normalizedLat = ((85f - lat) / 165f).coerceIn(0.05f, 0.95f)
    val y = normalizedLat * height
    return Offset(x, y)
}

// Representative landmass point coordinates (lat, lon) to draw stylized continents
private fun generateWorldGridPoints(): List<Pair<Float, Float>> {
    val points = mutableListOf<Pair<Float, Float>>()

    // North America
    for (lat in 25..60 step 5) {
        for (lon in -125..-70 step 6) {
            if (!(lat > 50 && lon > -85) && !(lat < 30 && lon < -110)) {
                points.add(lat.toFloat() to lon.toFloat())
            }
        }
    }
    // South America
    for (lat in -50..10 step 5) {
        for (lon in -75..-40 step 6) {
            if (!(lat < -35 && lon > -60)) {
                points.add(lat.toFloat() to lon.toFloat())
            }
        }
    }
    // Europe
    for (lat in 36..65 step 4) {
        for (lon in -10..40 step 5) {
            points.add(lat.toFloat() to lon.toFloat())
        }
    }
    // Africa
    for (lat in -34..35 step 5) {
        for (lon in -15..50 step 6) {
            if (!(lat > 20 && lon < 0) && !(lat < -15 && lon < 15)) {
                points.add(lat.toFloat() to lon.toFloat())
            }
        }
    }
    // Asia & Russia
    for (lat in 10..68 step 5) {
        for (lon in 42..145 step 7) {
            if (!(lat < 25 && lon > 125)) {
                points.add(lat.toFloat() to lon.toFloat())
            }
        }
    }
    // Australia & New Zealand
    for (lat in -38..-15 step 4) {
        for (lon in 115..152 step 5) {
            points.add(lat.toFloat() to lon.toFloat())
        }
    }
    points.add(-41f to 174f)
    points.add(-37f to 175f)

    return points
}
