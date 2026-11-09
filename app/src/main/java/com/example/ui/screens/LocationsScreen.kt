package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.ServerEntity
import com.example.data.model.DoubleVpnRoute
import com.example.ui.VpnViewModel
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

@Composable
fun LocationsScreen(
    viewModel: VpnViewModel,
    onServerSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val servers by viewModel.allServers.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val doubleVpnRoutes by viewModel.doubleVpnRoutes.collectAsState()
    val selectedDoubleRoute by viewModel.selectedDoubleVpnRoute.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Fastest", "Asia", "Middle East", "Europe", "Americas", "Africa", "Oceania", "Dedicated IP")

    // Sorted strictly alphabetically by Country Name in uniform list (A-Z)
    val sortedStandardServers = remember(servers, searchQuery, selectedFilter) {
        servers.filter { srv ->
            val matchesSearch = srv.countryName.contains(searchQuery, ignoreCase = true) ||
                    srv.cityName.contains(searchQuery, ignoreCase = true) ||
                    srv.endpointIp.contains(searchQuery, ignoreCase = true) ||
                    srv.countryCode.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Fastest" -> srv.latencyMs <= 18
                "Europe" -> listOf(
                    "DE", "GB", "FR", "NL", "CH", "IT", "ES", "SE", "NO", "FI",
                    "DK", "BE", "AT", "PL", "IE", "CZ", "PT", "RO", "HU", "GR",
                    "UA", "HR", "SK", "SI", "BG", "EE", "LV", "LT", "LU", "CY",
                    "AL", "AD", "MD", "MC", "ME", "RS", "IS", "BA", "MT", "MK"
                ).contains(srv.countryCode)
                "Asia" -> listOf(
                    "PK", "IN", "JP", "SG", "KR", "HK", "TW", "MY", "TH", "ID",
                    "PH", "VN", "BD", "NP", "LK", "KZ", "UZ", "MN", "AM", "AZ",
                    "GE", "AF", "KH", "CN", "RU"
                ).contains(srv.countryCode)
                "Middle East" -> listOf(
                    "AE", "SA", "QA", "KW", "BH", "OM", "TR", "IL", "JO", "LB", "IQ", "IR"
                ).contains(srv.countryCode)
                "Americas" -> listOf(
                    "US", "CA", "MX", "BR", "AR", "CL", "CO", "PE", "CR", "PA",
                    "EC", "UY", "VE", "BO", "PY"
                ).contains(srv.countryCode)
                "Africa" -> listOf("DZ", "EG", "NG", "ZA", "KE", "MA", "GH", "TN", "ET").contains(srv.countryCode)
                "Oceania" -> listOf("AU", "NZ").contains(srv.countryCode)
                "Dedicated IP" -> srv.supportsDedicatedIp
                else -> true
            }

            matchesSearch && matchesFilter
        }.sortedWith(compareBy({ it.countryName.lowercase() }, { it.cityName.lowercase() }))
    }

    // Filtered 20 Double VPN routes
    val filteredDoubleRoutes = remember(doubleVpnRoutes, searchQuery) {
        if (searchQuery.isBlank()) {
            doubleVpnRoutes
        } else {
            doubleVpnRoutes.filter { route ->
                route.title.contains(searchQuery, ignoreCase = true) ||
                        route.entryCountry.contains(searchQuery, ignoreCase = true) ||
                        route.exitCountry.contains(searchQuery, ignoreCase = true) ||
                        route.entryCity.contains(searchQuery, ignoreCase = true) ||
                        route.exitCity.contains(searchQuery, ignoreCase = true) ||
                        route.exitIp.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VpnBackgroundDark)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_vpn_logo),
                    contentDescription = "Proton Plus VPN Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.2.dp, VpnProtectedGreen.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Network Locations",
                        color = VpnTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedTabIndex == 0) "Sorted A-Z • ${sortedStandardServers.size} Locations" else "Multi-Hop Double VPN • 20 Chained Routes",
                        color = VpnTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VpnSurfaceCard)
                    .border(1.dp, VpnProtectedGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (selectedTabIndex == 0) "${sortedStandardServers.size} Nodes" else "20 Multi-Hop",
                    color = VpnProtectedGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: Standard (A-Z) vs Double VPN (Multi-Hop)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = VpnSurfaceDark,
            contentColor = VpnProtectedGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = VpnProtectedGreen,
                    height = 3.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(VpnSurfaceBorder)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, VpnSurfaceBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Standard",
                            tint = if (selectedTabIndex == 0) VpnProtectedGreen else VpnTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "All Countries (A-Z)",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTabIndex == 0) VpnProtectedGreen else VpnTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Double VPN",
                            tint = if (selectedTabIndex == 1) VpnAccentCyan else VpnTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Double VPN (20)",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTabIndex == 1) VpnAccentCyan else VpnTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = if (selectedTabIndex == 0) "Search country name (A-Z), city, IP..." else "Search double VPN (e.g. Canada, USA, Swiss)...",
                    color = VpnTextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = VpnTextSecondary)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = VpnSurfaceDark,
                unfocusedContainerColor = VpnSurfaceDark,
                focusedBorderColor = if (selectedTabIndex == 0) VpnProtectedGreen else VpnAccentCyan,
                unfocusedBorderColor = VpnSurfaceBorder,
                focusedTextColor = VpnTextPrimary,
                unfocusedTextColor = VpnTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("location_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTabIndex == 0) {
            // Standard Countries list: Sorted strictly A-Z in uniform single-line layout
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) VpnProtectedGreen else VpnSurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) VpnProtectedGreen else VpnSurfaceBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.Black else VpnTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Uniform, continuous A-Z list without fragmented section headers
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("standard_locations_list")
            ) {
                items(sortedStandardServers, key = { it.id }) { server ->
                    val isCurrent = server.id == selectedServer?.id && selectedDoubleRoute == null
                    UniformServerRow(
                        server = server,
                        isSelected = isCurrent,
                        onClick = {
                            viewModel.selectServer(server)
                            onServerSelected()
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Multi-Hop Double VPN Tab: 20 Chained collaborative secure connections
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F1A2E), Color(0xFF0D1B2A))
                        )
                    )
                    .border(1.dp, VpnAccentCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(VpnAccentCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = VpnAccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Double Encryption & Multi-Hop Chaining",
                            color = VpnAccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connects Hop 1 entry first, tunnels privately to Hop 2 exit node. Zero DNS/IP leaks.",
                            color = VpnTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("double_vpn_routes_list")
            ) {
                items(filteredDoubleRoutes, key = { it.id }) { route ->
                    val isSelected = selectedDoubleRoute?.id == route.id ||
                            (selectedServer?.id == route.id)

                    DoubleVpnRouteItem(
                        route = route,
                        isSelected = isSelected,
                        onClick = {
                            viewModel.selectDoubleVpnRoute(route)
                            onServerSelected()
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Standard uniform server item in a single, clean line format ("aik line main").
 * Arranged strictly alphabetically by country name.
 */
@Composable
private fun UniformServerRow(
    server: ServerEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val latencyColor = when {
        server.latencyMs < 20 -> VpnProtectedGreen
        server.latencyMs < 35 -> VpnConnectingYellow
        else -> Color(0xFFFF7675)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) VpnSurfaceCard else VpnSurfaceDark)
            .border(
                1.2.dp,
                if (isSelected) VpnProtectedGreen else VpnSurfaceBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("server_item_${server.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Flag and Country/City details in uniform layout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.countryName,
                            color = VpnTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${server.cityName}",
                            color = VpnTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (server.supportsDedicatedIp) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VpnAccentPurple.copy(alpha = 0.3f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "STATIC",
                                    color = VpnAccentCyan,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "IP: ${server.endpointIp}",
                            color = VpnTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { server.loadPercent / 100f },
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .clip(CircleShape),
                            color = latencyColor,
                            trackColor = VpnSurfaceBorder
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${server.loadPercent}%",
                            color = VpnTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Right: Ping and Selection checkmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(VpnSurfaceCard)
                        .border(1.dp, latencyColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${server.latencyMs} ms",
                        color = latencyColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSelected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = VpnProtectedGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Double VPN (Multi-Hop) chained collaborative connection card.
 * Clearly displays Entry Hop (Country/IP) ➔ Exit Hop (Country/IP) with dual encryption.
 */
@Composable
private fun DoubleVpnRouteItem(
    route: DoubleVpnRoute,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val latencyColor = when {
        route.latencyMs < 20 -> VpnProtectedGreen
        route.latencyMs < 35 -> VpnConnectingYellow
        else -> Color(0xFFFF7675)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) VpnSurfaceCard else VpnSurfaceDark)
            .border(
                1.5.dp,
                if (isSelected) VpnAccentCyan else VpnSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("double_vpn_${route.id}")
    ) {
        Column {
            // Top row: Flags chain, Route Title, Ping & Selected indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = route.entryFlag, fontSize = 22.sp)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "To",
                        tint = VpnAccentCyan,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(14.dp)
                    )
                    Text(text = route.exitFlag, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = route.title,
                            color = VpnTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${route.entryCity} ➔ ${route.exitCity}",
                            color = VpnTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VpnSurfaceCard)
                            .border(1.dp, latencyColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${route.latencyMs} ms",
                            color = latencyColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active Double Route",
                            tint = VpnAccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle row: Ingress & Egress Protected IPs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1A28))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Entry: ",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = route.entryIp,
                        color = VpnTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VpnAccentCyan.copy(alpha = 0.6f),
                    modifier = Modifier.size(12.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Exit IP: ",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${route.exitIp} (Shielded)",
                        color = VpnProtectedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: Dual Encryption & Capacity specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VpnAccentPurple.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DOUBLE WIREGUARD",
                            color = VpnAccentCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Load: ${route.loadPercent}%",
                        color = VpnTextMuted,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "${route.capacityGbps}G Mesh Capacity",
                    color = VpnTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
