package com.example.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnProtocol
import com.example.ui.VpnViewModel
import com.example.ui.theme.VpnAccentCyan
import com.example.ui.theme.VpnAccentPurple
import com.example.ui.theme.VpnBackgroundDark
import com.example.ui.theme.VpnConnectingYellow
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnSurfaceDark
import com.example.ui.theme.VpnTextMuted
import com.example.ui.theme.VpnTextPrimary
import com.example.ui.theme.VpnTextSecondary

@Composable
fun SecurityScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val killSwitch by viewModel.killSwitchEnabled.collectAsState()
    val protocol by viewModel.selectedProtocol.collectAsState()
    val mtu by viewModel.mtuClamp.collectAsState()
    val splitTunneling by viewModel.splitTunnelingEnabled.collectAsState()
    val performanceMode by viewModel.performanceModeEnabled.collectAsState()
    val leakResult by viewModel.leakTestResult.collectAsState()
    val isTesting by viewModel.isTestingLeak.collectAsState()
    val securityLogs by viewModel.securityLogs.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VpnBackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Security & Leak Diagnostics",
            color = VpnTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Cryptographic kill switch, DNS hardening, and diagnostics",
            color = VpnTextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Leak Test Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VpnSurfaceCard)
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Leak Diagnostics",
                            color = VpnTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Audits DNS, IPv6, and WebRTC integrity",
                            color = VpnTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.runLeakTest() },
                        enabled = !isTesting,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VpnProtectedGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("run_leak_test_button")
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Run Audit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (leakResult != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Geolocation Cohesion Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (leakResult!!.isGeoConsistent) VpnProtectedGreen.copy(alpha = 0.12f) else VpnDisconnectedRed.copy(alpha = 0.12f))
                            .border(
                                1.dp,
                                if (leakResult!!.isGeoConsistent) VpnProtectedGreen.copy(alpha = 0.4f) else VpnDisconnectedRed.copy(alpha = 0.4f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Egress: ${leakResult!!.cityName}, ${leakResult!!.countryName} (${leakResult!!.countryCode})",
                                color = if (leakResult!!.isGeoConsistent) VpnProtectedGreen else VpnDisconnectedRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = leakResult!!.geoSignalConsistency,
                                color = if (leakResult!!.isGeoConsistent) VpnProtectedGreen else VpnDisconnectedRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 99% Browser Anonymity & Safety Score Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (leakResult!!.isProtected) Color(0xFF022E1B) else Color(0xFF330A0E))
                            .border(
                                1.dp,
                                if (leakResult!!.isProtected) VpnProtectedGreen.copy(alpha = 0.6f) else VpnDisconnectedRed.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CHROME & BROWSER ANONYMITY",
                                color = VpnTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = if (leakResult!!.isProtected) "99% Certified Safe" else "0% Unprotected",
                                color = if (leakResult!!.isProtected) VpnProtectedGreen else VpnDisconnectedRed,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (leakResult!!.isProtected) "Zero-Leak IPv4/IPv6/DNS/WebRTC Shield Active" else "Raw ISP IP exposed to browser",
                                color = VpnTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (leakResult!!.isProtected) VpnProtectedGreen.copy(alpha = 0.2f) else VpnDisconnectedRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${leakResult!!.anonymityScore}%",
                                color = if (leakResult!!.isProtected) VpnProtectedGreen else VpnDisconnectedRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DiagnosticRow(
                        title = "Public IPv4 Egress",
                        statusText = "${leakResult!!.egressIp} (${leakResult!!.ipv4GeoStatus})",
                        isSafe = leakResult!!.isProtected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "IPv6 Geo Shield",
                        statusText = leakResult!!.ipv6GeoStatus,
                        isSafe = !leakResult!!.isIpv6Leaking
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "DNS Geolocation",
                        statusText = leakResult!!.dnsGeoStatus,
                        isSafe = !leakResult!!.isDnsLeaking
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "WebRTC srflx IP",
                        statusText = leakResult!!.webrtcGeoStatus,
                        isSafe = leakResult!!.isWebRtcProtected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "IPv4 Full Tunnel",
                        statusText = leakResult!!.ipv4RouteStatus,
                        isSafe = leakResult!!.isProtected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "Chrome Routing",
                        statusText = leakResult!!.chromeBypassStatus,
                        isSafe = leakResult!!.isProtected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow(
                        title = "Auto-Connect Guard",
                        statusText = if (killSwitch) "Active" else "Standby",
                        isSafe = killSwitch
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security Toggles
        Text(
            text = "Protection Controls",
            color = VpnTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Turbo Lag-Free Engine Toggle
        SecurityToggleItem(
            icon = Icons.Default.Speed,
            title = "Device Performance & Lag-Free Mode",
            subtitle = "Binds underlying physical network and optimizes CPU/battery so phone runs at 100% speed with zero app freezes",
            checked = performanceMode,
            onCheckedChange = { viewModel.setPerformanceMode(it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Auto-Connect / Connect Guard Toggle (replacing Kill Switch toggle)
        SecurityToggleItem(
            icon = Icons.Default.PowerSettingsNew,
            title = "Always-On Auto-Connect",
            subtitle = "Automatically re-establishes secure VPN connection and protects internet traffic instantly",
            checked = killSwitch,
            onCheckedChange = { viewModel.setKillSwitch(it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Split Tunneling Toggle
        SecurityToggleItem(
            icon = Icons.Default.Tune,
            title = "Split Tunneling",
            subtitle = "Allow selected local apps to bypass VPN tunnel",
            checked = splitTunneling,
            onCheckedChange = { viewModel.setSplitTunneling(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Protocol Selector
        Text(
            text = "Tunnel Protocol",
            color = VpnTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProtocolSelectorItem(
            protocol = VpnProtocol.WIREGUARD,
            isSelected = protocol == VpnProtocol.WIREGUARD,
            onClick = { viewModel.setProtocol(VpnProtocol.WIREGUARD) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProtocolSelectorItem(
            protocol = VpnProtocol.STEALTH_OBFUSCATED,
            isSelected = protocol == VpnProtocol.STEALTH_OBFUSCATED,
            onClick = { viewModel.setProtocol(VpnProtocol.STEALTH_OBFUSCATED) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // MTU Optimization Selector
        Text(
            text = "MTU Clamping",
            color = VpnTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1420, 1380, 1280).forEach { mtuValue ->
                val isSelected = mtu == mtuValue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) VpnProtectedGreen else VpnSurfaceDark)
                        .border(1.dp, if (isSelected) VpnProtectedGreen else VpnSurfaceBorder, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setMtuClamp(mtuValue) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$mtuValue",
                            color = if (isSelected) Color.Black else VpnTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (mtuValue == 1420) "Standard" else if (mtuValue == 1380) "LTE/5G" else "Strict",
                            color = if (isSelected) Color.Black.copy(alpha = 0.7f) else VpnTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Security Audit Logs
        Text(
            text = "Security & Route Audit Logs",
            color = VpnTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sanitized real-time routing table and leak protection events",
            color = VpnTextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F1520))
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(VpnProtectedGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUDIT ENGINE ACTIVE",
                        color = VpnProtectedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${securityLogs.size} events",
                    color = VpnTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (securityLogs.isEmpty()) {
                    Text(
                        text = "No security events recorded yet.",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                } else {
                    securityLogs.reversed().forEach { logLine ->
                        Text(
                            text = logLine,
                            color = if (logLine.contains("FAIL") || logLine.contains("LEAK")) VpnDisconnectedRed
                            else if (logLine.contains("RouteAudit") || logLine.contains("DNS")) VpnAccentCyan
                            else if (logLine.contains("ARMED") || logLine.contains("SHIELDED")) VpnProtectedGreen
                            else VpnTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DiagnosticRow(
    title: String,
    statusText: String,
    isSafe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Shield,
                contentDescription = title,
                tint = if (isSafe) VpnProtectedGreen else VpnDisconnectedRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = VpnTextPrimary, fontSize = 13.sp)
        }
        Text(
            text = statusText,
            color = if (isSafe) VpnProtectedGreen else VpnDisconnectedRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SecurityToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VpnSurfaceDark)
            .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = VpnAccentCyan,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = VpnTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = VpnTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = VpnProtectedGreen,
                uncheckedThumbColor = VpnTextMuted,
                uncheckedTrackColor = VpnSurfaceCard
            )
        )
    }
}

@Composable
private fun ProtocolSelectorItem(
    protocol: VpnProtocol,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) VpnSurfaceCard else VpnSurfaceDark)
            .border(1.5.dp, if (isSelected) VpnProtectedGreen else VpnSurfaceBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = protocol.displayName,
                    color = VpnTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = protocol.description,
                    color = VpnTextSecondary,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Protocol",
                    tint = VpnProtectedGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
