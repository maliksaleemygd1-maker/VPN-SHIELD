package com.example.ui.screens

import android.app.Activity
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ConnectionStatus
import com.example.data.model.VpnProtocol
import com.example.ui.VpnViewModel
import com.example.ui.components.ShieldButton
import com.example.ui.theme.VpnAccentCyan
import com.example.ui.theme.VpnBackgroundDark
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnSurfaceDark
import com.example.ui.theme.VpnTextMuted
import com.example.ui.theme.VpnTextPrimary
import com.example.ui.theme.VpnTextSecondary
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: VpnViewModel,
    onNavigateToLocations: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val status by viewModel.connectionStatus.collectAsState()
    val server by viewModel.selectedServer.collectAsState()
    val downloadSpeed by viewModel.currentDownloadSpeed.collectAsState()
    val uploadSpeed by viewModel.currentUploadSpeed.collectAsState()
    val durationSeconds by viewModel.connectedDurationSeconds.collectAsState()
    val totalDownloaded by viewModel.totalBytesDownloaded.collectAsState()
    val totalUploaded by viewModel.totalBytesUploaded.collectAsState()
    val netShieldEnabled by viewModel.netShieldEnabled.collectAsState()
    val netShieldBlockedCount by viewModel.netShieldBlockedCount.collectAsState()
    val autoConnectWifi by viewModel.autoConnectWifi.collectAsState()
    val selectedProtocol by viewModel.selectedProtocol.collectAsState()

    val isConnected = status == ConnectionStatus.CONNECTED
    val isConnecting = status == ConnectionStatus.CONNECTING
    var showAnonymityDialog by remember { mutableStateOf(false) }

    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.toggleConnection()
        } else {
            Toast.makeText(context, "VPN system permission is required to shield your network", Toast.LENGTH_LONG).show()
        }
    }

    val scrollState = rememberScrollState()

    // Format connection duration: Hours, Minutes, Seconds
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VpnBackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header Bar with App Identity and Connect Option in place of Kill Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_vpn_logo),
                    contentDescription = "Proton VPN Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.5.dp,
                            if (isConnected) VpnProtectedGreen else Color(0xFFFF3B30).copy(alpha = 0.7f),
                            RoundedCornerShape(12.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Proton Plus VPN",
                        color = VpnTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isConnected) VpnProtectedGreen
                                    else if (isConnecting) Color(0xFFF59E0B)
                                    else Color(0xFFFF3B30)
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isConnected) "Connected & Secure" else if (isConnecting) "Connecting..." else "Disconnected",
                            color = if (isConnected) VpnProtectedGreen else if (isConnecting) Color(0xFFF59E0B) else Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Top Connect Option Badge (in place of kill switch)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isConnected) Color(0xFF07301B)
                        else if (isConnecting) Color(0xFF2E1C02)
                        else Color(0xFF33090E)
                    )
                    .border(
                        1.5.dp,
                        if (isConnected) VpnProtectedGreen
                        else if (isConnecting) Color(0xFFF59E0B)
                        else Color(0xFFFF3B30),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        if (status == ConnectionStatus.DISCONNECTED) {
                            val prepareIntent = VpnService.prepare(context)
                            if (prepareIntent != null) {
                                vpnPrepareLauncher.launch(prepareIntent)
                            } else {
                                viewModel.toggleConnection()
                            }
                        } else {
                            viewModel.toggleConnection()
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("top_connect_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.PowerSettingsNew,
                        contentDescription = "Connection Toggle",
                        tint = if (isConnected) VpnProtectedGreen else if (isConnecting) Color(0xFFF59E0B) else Color(0xFFFF453A),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isConnected) "CONNECTED" else if (isConnecting) "CONNECTING" else "CONNECT",
                        color = if (isConnected) VpnProtectedGreen else if (isConnecting) Color(0xFFF59E0B) else Color(0xFFFF453A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Location Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VpnSurfaceDark)
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(16.dp))
                .clickable(onClick = onNavigateToLocations)
                .padding(horizontal = 16.dp, vertical = 13.dp)
                .testTag("active_server_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server?.flagEmoji ?: "🇩🇪",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${server?.cityName ?: "Frankfurt"}, ${server?.countryName ?: "Germany"}",
                            color = VpnTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isConnected) "Active Location: ${server?.endpointIp ?: "185.120.44.12"}" else "Selected Location (Tap to change)",
                            color = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VpnSurfaceCard)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${server?.latencyMs ?: 12} ms",
                            color = VpnProtectedGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Change Location",
                        tint = VpnTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Center Connect Button (Red when disconnected, Green when connected)
        ShieldButton(
            status = status,
            onToggle = {
                if (status == ConnectionStatus.DISCONNECTED) {
                    val prepareIntent = VpnService.prepare(context)
                    if (prepareIntent != null) {
                        vpnPrepareLauncher.launch(prepareIntent)
                    } else {
                        viewModel.toggleConnection()
                    }
                } else {
                    viewModel.toggleConnection()
                }
            }
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Download and Upload Speed Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Download Speed Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VpnSurfaceDark)
                    .border(
                        1.5.dp,
                        if (isConnected) VpnProtectedGreen.copy(alpha = 0.5f) else VpnSurfaceBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DOWNLOAD",
                            color = VpnTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color(0xFF05301D) else VpnSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Download Speed",
                                tint = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isConnected) String.format(Locale.US, "%.1f", downloadSpeed) else "0.0",
                        color = if (isConnected) VpnProtectedGreen else VpnTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Mbps",
                        color = VpnTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Upload Speed Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VpnSurfaceDark)
                    .border(
                        1.5.dp,
                        if (isConnected) VpnAccentCyan.copy(alpha = 0.5f) else VpnSurfaceBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "UPLOAD",
                            color = VpnTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color(0xFF082636) else VpnSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Upload Speed",
                                tint = if (isConnected) VpnAccentCyan else VpnTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isConnected) String.format(Locale.US, "%.1f", uploadSpeed) else "0.0",
                        color = if (isConnected) VpnAccentCyan else VpnTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Mbps",
                        color = VpnTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection Duration Timer Card (Hours, Minutes, Seconds)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isConnected) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF042416),
                                Color(0xFF071B12)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                VpnSurfaceDark,
                                VpnSurfaceDark
                            )
                        )
                    }
                )
                .border(
                    1.5.dp,
                    if (isConnected) VpnProtectedGreen.copy(alpha = 0.7f) else VpnSurfaceBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(vertical = 18.dp, horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Connection Timer",
                        tint = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "ACTIVE CONNECTION TIME" else "CONNECTION DURATION",
                        color = if (isConnected) VpnProtectedGreen else VpnTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Digits for Hours, Minutes, Seconds
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours block
                    TimeUnitBlock(
                        value = String.format(Locale.US, "%02d", hours),
                        label = "HOURS",
                        isActive = isConnected
                    )

                    Text(
                        text = ":",
                        color = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Minutes block
                    TimeUnitBlock(
                        value = String.format(Locale.US, "%02d", minutes),
                        label = "MINUTES",
                        isActive = isConnected
                    )

                    Text(
                        text = ":",
                        color = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Seconds block
                    TimeUnitBlock(
                        value = String.format(Locale.US, "%02d", seconds),
                        label = "SECONDS",
                        isActive = isConnected
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Data Volume Row (Downloaded & Uploaded Volume)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isConnected) Color(0xFF032617) else VpnSurfaceCard)
                        .border(
                            1.dp,
                            if (isConnected) VpnProtectedGreen.copy(alpha = 0.3f) else VpnSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Session Downloaded",
                            tint = if (isConnected) VpnProtectedGreen else VpnTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "SESSION DOWN",
                                color = VpnTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (isConnected) viewModel.formatBytes(totalDownloaded) else "0.0 KB",
                                color = if (isConnected) VpnProtectedGreen else VpnTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(VpnSurfaceBorder)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Session Uploaded",
                            tint = if (isConnected) VpnAccentCyan else VpnTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "SESSION UP",
                                color = VpnTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (isConnected) viewModel.formatBytes(totalUploaded) else "0.0 KB",
                                color = if (isConnected) VpnAccentCyan else VpnTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(VpnSurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "LATENCY",
                            color = VpnTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${server?.latencyMs ?: 14} ms",
                            color = if (isConnected) VpnProtectedGreen else VpnTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Virtual IP & Privacy Shield Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VpnSurfaceDark)
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy Shield",
                            tint = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIRTUAL IP & PRIVACY",
                            color = VpnTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isConnected) Color(0xFF063B20) else Color(0xFF3B0B0E)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isConnected) "PROTECTED" else "ISP EXPOSED",
                            color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VpnSurfaceCard)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isConnected) "VIRTUAL PUBLIC IP" else "REAL PHYSICAL IP",
                            color = VpnTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isConnected) (server?.endpointIp ?: "185.120.44.12") else "192.168.1.1 (Exposed)",
                            color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VpnSurfaceDark)
                            .clickable {
                                val ipToCopy = if (isConnected) (server?.endpointIp ?: "185.120.44.12") else "185.120.44.12"
                                clipboardManager.setText(AnnotatedString(ipToCopy))
                                Toast.makeText(context, "Virtual IP copied: $ipToCopy", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy IP",
                            tint = VpnProtectedGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COPY",
                            color = VpnProtectedGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• DNS: 1.1.1.1 (Zero-Leak DoH)",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "• ChaCha20-Poly1305",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chrome & Browser Security Audit (99% Anonymity Rating)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isConnected) Color(0xFF032617) else Color(0xFF2E0C10))
                        .border(
                            1.dp,
                            if (isConnected) VpnProtectedGreen.copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { showAnonymityDialog = true }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isConnected) VpnProtectedGreen.copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Safety Score",
                                        tint = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CHROME & BROWSER SAFETY",
                                        color = VpnTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    )
                                    Text(
                                        text = if (isConnected) "99% Safe (Maximum Anonymity)" else "0% Safe (Raw ISP Exposed)",
                                        color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isConnected) VpnProtectedGreen else Color(0xFFFF5252))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isConnected) "99% SAFE" else "EXPOSED",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4 Security Vectors (IPv4, IPv6, DNS, WebRTC)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "IPv4 MASK", color = VpnTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isConnected) "100% Cloaked" else "Exposed",
                                    color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "IPv6 SHIELD", color = VpnTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isConnected) "::/0 Trapped" else "Leaking",
                                    color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "DNS LEAK", color = VpnTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isConnected) "Zero-Leak DoH" else "Exposed",
                                    color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "WEBRTC STUN", color = VpnTextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isConnected) "Shielded" else "Leaking",
                                    color = if (isConnected) VpnProtectedGreen else Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap to view test details & how to get 100% in Chrome ➔",
                            color = VpnTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Essential VPN Quick Shields Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VpnSurfaceDark)
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ESSENTIAL SHIELDS & CONTROLS",
                    color = VpnTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // NetShield™ Ad & Malware Blocker Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (netShieldEnabled) Color(0xFF052B17) else VpnSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "NetShield",
                                tint = if (netShieldEnabled) VpnProtectedGreen else VpnTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NetShield™ Ad & Tracker Guard",
                                    color = VpnTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (netShieldEnabled && netShieldBlockedCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF084123))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$netShieldBlockedCount blocked",
                                            color = VpnProtectedGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Blocks malware domains, trackers & intrusive ads",
                                color = VpnTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = netShieldEnabled,
                        onCheckedChange = { viewModel.toggleNetShield(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = VpnProtectedGreen,
                            uncheckedThumbColor = VpnTextMuted,
                            uncheckedTrackColor = VpnSurfaceCard
                        ),
                        modifier = Modifier.testTag("switch_netshield")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(VpnSurfaceBorder))
                Spacer(modifier = Modifier.height(14.dp))

                // Public Wi-Fi Auto-Guard Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (autoConnectWifi) Color(0xFF052B17) else VpnSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Auto Connect Wi-Fi",
                                tint = if (autoConnectWifi) VpnProtectedGreen else VpnTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Public Wi-Fi Auto-Protect",
                                color = VpnTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Automatically arm VPN when joining untrusted hotspots",
                                color = VpnTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = autoConnectWifi,
                        onCheckedChange = { viewModel.toggleAutoConnectWifi(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = VpnProtectedGreen,
                            uncheckedThumbColor = VpnTextMuted,
                            uncheckedTrackColor = VpnSurfaceCard
                        ),
                        modifier = Modifier.testTag("switch_auto_wifi")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(VpnSurfaceBorder))
                Spacer(modifier = Modifier.height(14.dp))

                // Protocol Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF032617)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Protocol",
                                tint = VpnProtectedGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tunnel Protocol Engine",
                                color = VpnTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (selectedProtocol == VpnProtocol.WIREGUARD) "WireGuard® (Max Speed)" else "Stealth TLS (Bypass Blocks)",
                                color = VpnTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedProtocol == VpnProtocol.WIREGUARD) VpnProtectedGreen else VpnSurfaceCard)
                                .clickable { viewModel.setProtocol(VpnProtocol.WIREGUARD) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "WireGuard",
                                color = if (selectedProtocol == VpnProtocol.WIREGUARD) Color.Black else VpnTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedProtocol == VpnProtocol.STEALTH_OBFUSCATED) VpnProtectedGreen else VpnSurfaceCard)
                                .clickable { viewModel.setProtocol(VpnProtocol.STEALTH_OBFUSCATED) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Stealth",
                                color = if (selectedProtocol == VpnProtocol.STEALTH_OBFUSCATED) Color.Black else VpnTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    if (showAnonymityDialog) {
        AlertDialog(
            onDismissRequest = { showAnonymityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = VpnProtectedGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Browser & Network Safety (99%)",
                        color = VpnTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Why test sites (Whoer.net) may show 70% & How to make it 99%:",
                        color = VpnAccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Android VPN Key 🔑:\nEnsure you accepted the Android system 'Connection Request' dialog. The Key icon must appear in your top status bar.\n\n" +
                                "2. Time Zone Mismatch (-10% on Whoer):\nWhoer checks your phone's system clock. If your phone is set to your local country but connected to ${server?.countryName ?: "Germany"}, Whoer docks 10%. Go to Phone Settings > Date & Time > Set Time Zone to ${server?.countryName ?: "Germany"}.\n\n" +
                                "3. Chrome WebRTC (-10% on Whoer):\nChrome mobile has WebRTC STUN active. In Chrome Settings > Privacy & Security, enable 'Enhanced Protection'.\n\n" +
                                "4. In-Tunnel Network Security:\nIPv4: 100% Cloaked (${server?.endpointIp ?: "185.120.44.12"})\nIPv6: Trapped (::/0 Zero Leak)\nDNS: 1.1.1.1 Encrypted DoH",
                        color = VpnTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAnonymityDialog = false }) {
                    Text("Understood", color = VpnProtectedGreen, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = VpnSurfaceDark
        )
    }
}

@Composable
private fun TimeUnitBlock(
    value: String,
    label: String,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Color(0xFF03331E) else VpnSurfaceCard)
            .border(
                1.dp,
                if (isActive) VpnProtectedGreen.copy(alpha = 0.5f) else VpnSurfaceBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            color = if (isActive) VpnProtectedGreen else VpnTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            color = if (isActive) VpnProtectedGreen.copy(alpha = 0.8f) else VpnTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

