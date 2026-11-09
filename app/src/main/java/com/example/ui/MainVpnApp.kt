package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DedicatedIpScreen
import com.example.ui.screens.LocationsScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.theme.VpnBackgroundDark
import com.example.ui.theme.VpnProtectedGreen
import com.example.ui.theme.VpnSurfaceBorder
import com.example.ui.theme.VpnSurfaceDark
import com.example.ui.theme.VpnTextMuted
import com.example.ui.theme.VpnTextPrimary

@Composable
fun MainVpnApp(
    viewModel: VpnViewModel = viewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = VpnBackgroundDark,
        bottomBar = {
            NavigationBar(
                containerColor = VpnSurfaceDark,
                contentColor = VpnTextPrimary,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Connect Tab",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Connect",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = VpnProtectedGreen,
                        indicatorColor = VpnProtectedGreen,
                        unselectedIconColor = VpnTextMuted,
                        unselectedTextColor = VpnTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_connect")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Locations Tab",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Locations",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = VpnProtectedGreen,
                        indicatorColor = VpnProtectedGreen,
                        unselectedIconColor = VpnTextMuted,
                        unselectedTextColor = VpnTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_locations")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Dedicated IP Tab",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Static IP",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = VpnProtectedGreen,
                        indicatorColor = VpnProtectedGreen,
                        unselectedIconColor = VpnTextMuted,
                        unselectedTextColor = VpnTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_dedicated_ip")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Tab",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Security",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = VpnProtectedGreen,
                        indicatorColor = VpnProtectedGreen,
                        unselectedIconColor = VpnTextMuted,
                        unselectedTextColor = VpnTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_security")
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> DashboardScreen(
                viewModel = viewModel,
                onNavigateToLocations = { selectedTab = 1 },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> LocationsScreen(
                viewModel = viewModel,
                onServerSelected = { selectedTab = 0 },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> DedicatedIpScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> SecurityScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
