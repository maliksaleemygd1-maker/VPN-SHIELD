package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.CountryIpPool
import com.example.data.model.StaticIpAddress
import com.example.ui.VpnViewModel
import com.example.ui.theme.VpnAccentCyan
import com.example.ui.theme.VpnAccentPurple
import com.example.ui.theme.VpnBackgroundDark
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnSurfaceDark
import com.example.ui.theme.VpnTextMuted
import com.example.ui.theme.VpnTextPrimary
import com.example.ui.theme.VpnTextSecondary

@Composable
fun DedicatedIpScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val status by viewModel.connectionStatus.collectAsState()
    val isConnected = status == ConnectionStatus.CONNECTED

    val countryPools by viewModel.countryIpPools.collectAsState()
    val selectedPool by viewModel.selectedCountryPool.collectAsState()
    val assignedIp by viewModel.assignedStaticIp.collectAsState()
    val selectedCountryCode by viewModel.selectedPoolCountryCode.collectAsState()
    val searchQuery by viewModel.staticIpSearchQuery.collectAsState()
    val selectedTagFilter by viewModel.staticIpTagFilter.collectAsState()

    val availableTags = listOf(
        "All",
        "Banking & Financial",
        "4K UHD Streaming",
        "Low-Latency Gaming",
        "Corporate SSH Bastion",
        "Zero-CAPTCHA Clean Web",
        "Clean Residential Egress"
    )

    // Filter IPs for selected country
    val filteredIps = (selectedPool?.ips ?: emptyList()).filter { ipItem ->
        val matchesSearch = searchQuery.isBlank() ||
                ipItem.ip.contains(searchQuery.trim(), ignoreCase = true) ||
                ipItem.cityName.contains(searchQuery.trim(), ignoreCase = true) ||
                ipItem.tag.contains(searchQuery.trim(), ignoreCase = true) ||
                ipItem.asn.contains(searchQuery.trim(), ignoreCase = true)

        val matchesTag = selectedTagFilter == "All" || ipItem.tag.equals(selectedTagFilter, ignoreCase = true)

        matchesSearch && matchesTag
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VpnBackgroundDark)
            .padding(horizontal = 18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Title & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Static IP Pool Manager",
                        color = VpnTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Country-Wise 20 Static IPs • 100% Clean Reputation",
                        color = VpnTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VpnSurfaceCard)
                        .border(1.dp, VpnAccentCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "20 IPs / Pool",
                        color = VpnAccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currently Assigned Static IP Banner (Hero Card)
            if (assignedIp != null) {
                ActiveStaticIpBanner(
                    staticIp = assignedIp!!,
                    isConnected = isConnected,
                    onDisconnect = { viewModel.toggleConnection() },
                    onRelease = { viewModel.releaseStaticIp() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Country Pool Picker Label
            Text(
                text = "Select Country Pool (20 Static IPs Each)",
                color = VpnTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Horizontal Country Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(countryPools, key = { it.countryCode }) { pool ->
                    val isSelected = pool.countryCode.equals(selectedCountryCode, ignoreCase = true)
                    CountryPoolChip(
                        pool = pool,
                        isSelected = isSelected,
                        onClick = { viewModel.selectPoolCountry(pool.countryCode) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Pool Overview & Quality Guarantee Card
        item {
            if (selectedPool != null) {
                PoolQualityOverviewCard(pool = selectedPool!!)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Search Bar & Purpose Tags
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setStaticIpSearchQuery(it) },
                placeholder = {
                    Text("Search static IP, city, or ASN...", color = VpnTextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = VpnTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setStaticIpSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = VpnTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("static_ip_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VpnSurfaceDark,
                    unfocusedContainerColor = VpnSurfaceDark,
                    focusedBorderColor = VpnAccentCyan,
                    unfocusedBorderColor = VpnSurfaceBorder,
                    focusedTextColor = VpnTextPrimary,
                    unfocusedTextColor = VpnTextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Purpose Tag Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableTags) { tag ->
                    val isTagSelected = tag == selectedTagFilter
                    TagFilterChip(
                        tag = tag,
                        isSelected = isTagSelected,
                        onClick = { viewModel.setStaticIpTagFilter(tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedPool?.countryName ?: "Country"} Pool: ${filteredIps.size} of 20 Static IPs",
                    color = VpnTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "100% Clean • 0% Abuse",
                    color = VpnProtectedGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // List of 20 Static IPs
        items(filteredIps, key = { it.id }) { staticIp ->
            val isCurrentAssigned = assignedIp?.ip == staticIp.ip
            val isCurrentConnected = isCurrentAssigned && isConnected

            StaticIpItemCard(
                staticIp = staticIp,
                isAssigned = isCurrentAssigned,
                isConnected = isCurrentConnected,
                onConnect = { viewModel.connectToStaticIp(staticIp) }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Bottom Quality & Verification Footer
        item {
            Spacer(modifier = Modifier.height(14.dp))
            StaticIpAssuranceFooter()
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ActiveStaticIpBanner(
    staticIp: StaticIpAddress,
    isConnected: Boolean,
    onDisconnect: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2625),
                        VpnSurfaceCard
                    )
                )
            )
            .border(1.5.dp, VpnProtectedGreen, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = staticIp.flagEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ASSIGNED STATIC IP",
                            color = VpnProtectedGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${staticIp.cityName}, ${staticIp.countryName}",
                            color = VpnTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isConnected) VpnProtectedGreen else VpnSurfaceDark)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isConnected) "CONNECTED" else "ASSIGNED",
                        color = if (isConnected) Color.Black else VpnProtectedGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = staticIp.ip,
                color = VpnAccentCyan,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subnet: ${staticIp.subnet} • ${staticIp.asn}",
                    color = VpnTextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "${staticIp.latencyMs} ms • 0% Abuse",
                    color = VpnProtectedGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDisconnect,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("toggle_static_connection_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) VpnDisconnectedRed else VpnProtectedGreen,
                        contentColor = if (isConnected) Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = if (isConnected) "Disconnect" else "Connect Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onRelease,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("release_static_ip_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VpnSurfaceDark,
                        contentColor = VpnTextMuted
                    )
                ) {
                    Text(
                        text = "Release to Pool",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryPoolChip(
    pool: CountryIpPool,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) VpnProtectedGreen.copy(alpha = 0.15f) else VpnSurfaceDark)
            .border(
                1.dp,
                if (isSelected) VpnProtectedGreen else VpnSurfaceBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = pool.flagEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = pool.countryName,
                color = if (isSelected) VpnProtectedGreen else VpnTextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(20)",
                color = if (isSelected) VpnProtectedGreen else VpnTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PoolQualityOverviewCard(pool: CountryIpPool) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VpnSurfaceDark)
            .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Pool",
                        tint = VpnProtectedGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${pool.countryName} Static IP Pool Quality",
                        color = VpnTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "AS209498 Proton AG",
                    color = VpnAccentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QualityMetricItem(label = "Allocated", value = "20 Static IPs")
                QualityMetricItem(label = "Reputation", value = "100% Clean")
                QualityMetricItem(label = "Abuse Score", value = "0.0% Abuse")
                QualityMetricItem(label = "Avg Latency", value = "${pool.averagePingMs} ms")
            }
        }
    }
}

@Composable
private fun QualityMetricItem(label: String, value: String) {
    Column {
        Text(text = label, color = VpnTextMuted, fontSize = 10.sp)
        Text(text = value, color = VpnProtectedGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TagFilterChip(
    tag: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) VpnAccentCyan.copy(alpha = 0.15f) else VpnSurfaceCard)
            .border(
                1.dp,
                if (isSelected) VpnAccentCyan else VpnSurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = tag,
            color = if (isSelected) VpnAccentCyan else VpnTextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StaticIpItemCard(
    staticIp: StaticIpAddress,
    isAssigned: Boolean,
    isConnected: Boolean,
    onConnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VpnSurfaceDark)
            .border(
                1.dp,
                if (isConnected) VpnProtectedGreen else if (isAssigned) VpnAccentCyan else VpnSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VpnProtectedGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = staticIp.ip,
                        color = VpnTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(VpnProtectedGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "100% CLEAN",
                        color = VpnProtectedGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${staticIp.flagEmoji} ${staticIp.cityName} • ${staticIp.subnet}",
                    color = VpnTextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "${staticIp.latencyMs} ms • ${staticIp.loadPercent}% load",
                    color = VpnTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VpnSurfaceCard)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = staticIp.ipType,
                            color = VpnAccentPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VpnSurfaceCard)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = staticIp.tag,
                            color = VpnAccentCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onConnect,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("connect_static_ip_${staticIp.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) VpnSurfaceCard else VpnProtectedGreen,
                        contentColor = if (isConnected) VpnProtectedGreen else Color.Black
                    )
                ) {
                    if (isConnected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Active",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = if (isAssigned) "Assigned" else "Connect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StaticIpAssuranceFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VpnSurfaceDark)
            .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Static IP Quality & Security Standards",
            color = VpnTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        QualityPointRow(
            icon = Icons.Default.Security,
            title = "0% Abuse Score Guarantee",
            description = "Each IP is audited against AbuseIPDB, Spamhaus ZEN, and Scamalytics for 100% clean reputation."
        )
        Spacer(modifier = Modifier.height(8.dp))

        QualityPointRow(
            icon = Icons.Default.Lock,
            title = "Zero-CAPTCHA & Banking Integrity",
            description = "Dedicated static addresses prevent Cloudflare security challenges and bank risk lockouts."
        )
        Spacer(modifier = Modifier.height(8.dp))

        QualityPointRow(
            icon = Icons.Default.VpnKey,
            title = "Persistent 1:1 NAT Mapping",
            description = "Your outbound IPv4 remains strictly consistent across sessions with full country geolocation alignment."
        )
    }
}

@Composable
private fun QualityPointRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = VpnAccentCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = VpnTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = VpnTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
