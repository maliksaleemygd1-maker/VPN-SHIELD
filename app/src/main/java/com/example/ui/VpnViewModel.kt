package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VpnDatabase
import com.example.data.local.entity.ServerEntity
import com.example.data.model.ConnectionStatus
import com.example.data.model.CountryIpPool
import com.example.data.model.DedicatedIpProfile
import com.example.data.model.LeakTestResult
import com.example.data.model.StaticIpAddress
import com.example.data.model.VpnProtocol
import com.example.data.repository.StaticIpPoolManager
import com.example.data.repository.VpnRepository
import com.example.vpn.ProtonVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VpnDatabase.getInstance(application)
    private val repository = VpnRepository(db.serverDao(), db.speedRecordDao())

    val allServers: StateFlow<List<ServerEntity>> = repository.allServers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val connectionStatus: StateFlow<ConnectionStatus> = ProtonVpnService.vpnStatus
    val currentDownloadSpeed: StateFlow<Float> = ProtonVpnService.currentSpeedDownload
    val currentUploadSpeed: StateFlow<Float> = ProtonVpnService.currentSpeedUpload
    val connectedDurationSeconds: StateFlow<Long> = ProtonVpnService.connectedDurationSeconds
    val totalBytesDownloaded: StateFlow<Long> = ProtonVpnService.totalBytesDownloaded
    val totalBytesUploaded: StateFlow<Long> = ProtonVpnService.totalBytesUploaded
    val netShieldBlockedCount: StateFlow<Int> = ProtonVpnService.netShieldBlockedCount
    val netShieldEnabled: StateFlow<Boolean> = ProtonVpnService.netShieldEnabled

    private val _autoConnectWifi = MutableStateFlow(true)
    val autoConnectWifi: StateFlow<Boolean> = _autoConnectWifi.asStateFlow()

    private val _selectedServer = MutableStateFlow<ServerEntity?>(null)
    val selectedServer: StateFlow<ServerEntity?> = _selectedServer.asStateFlow()

    private val _selectedProtocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val selectedProtocol: StateFlow<VpnProtocol> = _selectedProtocol.asStateFlow()

    private val _killSwitchEnabled = MutableStateFlow(true)
    val killSwitchEnabled: StateFlow<Boolean> = _killSwitchEnabled.asStateFlow()

    private val _mtuClamp = MutableStateFlow(1420)
    val mtuClamp: StateFlow<Int> = _mtuClamp.asStateFlow()

    private val _splitTunnelingEnabled = MutableStateFlow(false)
    val splitTunnelingEnabled: StateFlow<Boolean> = _splitTunnelingEnabled.asStateFlow()

    private val _leakTestResult = MutableStateFlow<LeakTestResult?>(null)
    val leakTestResult: StateFlow<LeakTestResult?> = _leakTestResult.asStateFlow()

    private val _isTestingLeak = MutableStateFlow(false)
    val isTestingLeak: StateFlow<Boolean> = _isTestingLeak.asStateFlow()

    private val _is10kHighConcurrencyMode = MutableStateFlow(true)
    val is10kHighConcurrencyMode: StateFlow<Boolean> = _is10kHighConcurrencyMode.asStateFlow()

    private val _performanceModeEnabled = MutableStateFlow(true)
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()

    private val _concurrentActiveUsers = MutableStateFlow(10482)
    val concurrentActiveUsers: StateFlow<Int> = _concurrentActiveUsers.asStateFlow()

    private val _clusterLoadPercent = MutableStateFlow(24)
    val clusterLoadPercent: StateFlow<Int> = _clusterLoadPercent.asStateFlow()

    val dedicatedIpProfile: DedicatedIpProfile = repository.getDedicatedProfile()

    // Static IP Pool Management
    private val _assignedStaticIp = MutableStateFlow<StaticIpAddress?>(null)
    val assignedStaticIp: StateFlow<StaticIpAddress?> = _assignedStaticIp.asStateFlow()

    private val _selectedPoolCountryCode = MutableStateFlow("DE")
    val selectedPoolCountryCode: StateFlow<String> = _selectedPoolCountryCode.asStateFlow()

    private val _staticIpSearchQuery = MutableStateFlow("")
    val staticIpSearchQuery: StateFlow<String> = _staticIpSearchQuery.asStateFlow()

    private val _staticIpTagFilter = MutableStateFlow("All")
    val staticIpTagFilter: StateFlow<String> = _staticIpTagFilter.asStateFlow()

    val countryIpPools: StateFlow<List<CountryIpPool>> = combine(
        allServers,
        _assignedStaticIp
    ) { servers, assigned ->
        StaticIpPoolManager.getAllCountryPools(servers, assigned?.ip)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedCountryPool: StateFlow<CountryIpPool?> = combine(
        countryIpPools,
        _selectedPoolCountryCode
    ) { pools, code ->
        pools.find { it.countryCode.equals(code, ignoreCase = true) }
            ?: pools.firstOrNull { it.countryCode == "DE" }
            ?: pools.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _doubleVpnRoutes = MutableStateFlow<List<com.example.data.model.DoubleVpnRoute>>(com.example.data.model.DoubleVpnSeedData.routes)
    val doubleVpnRoutes: StateFlow<List<com.example.data.model.DoubleVpnRoute>> = _doubleVpnRoutes.asStateFlow()

    private val _selectedDoubleVpnRoute = MutableStateFlow<com.example.data.model.DoubleVpnRoute?>(null)
    val selectedDoubleVpnRoute: StateFlow<com.example.data.model.DoubleVpnRoute?> = _selectedDoubleVpnRoute.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultServersSeeded()
            val fastest = repository.getFastestServer()
            if (_selectedServer.value == null) {
                _selectedServer.value = fastest
            }
        }

        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(3500)
                val delta = kotlin.random.Random.nextInt(-10, 15)
                _concurrentActiveUsers.value = (_concurrentActiveUsers.value + delta).coerceIn(10250, 11400)
                _clusterLoadPercent.value = (_concurrentActiveUsers.value / 410).coerceIn(21, 28)
            }
        }
    }

    fun toggle10kConcurrencyMode() {
        _is10kHighConcurrencyMode.value = !_is10kHighConcurrencyMode.value
    }

    fun selectServer(server: ServerEntity) {
        _selectedDoubleVpnRoute.value = null
        _selectedServer.value = server
        if (connectionStatus.value == ConnectionStatus.CONNECTED) {
            // Reconnect to newly selected server
            toggleConnection()
            toggleConnection()
        }
    }

    fun selectDoubleVpnRoute(route: com.example.data.model.DoubleVpnRoute) {
        _selectedDoubleVpnRoute.value = route
        val doubleServer = ServerEntity(
            id = route.id,
            countryCode = "DOUBLE",
            countryName = "${route.entryCountry} + ${route.exitCountry}",
            flagEmoji = "${route.entryFlag}➔${route.exitFlag}",
            cityName = "${route.entryCity} ➔ ${route.exitCity}",
            hostname = "${route.id}.double.protonplusvpn.net",
            endpointIp = route.exitIp,
            port = 51820,
            publicKey = "DoUbLeVpN256ChAcHa20PoLy1305SeCuReEgReSs01=",
            latencyMs = route.latencyMs,
            loadPercent = route.loadPercent,
            packetLossPercent = 0.0f,
            capacityMbps = route.capacityGbps * 1000,
            supportsDedicatedIp = false,
            isFavorite = false,
            latitude = 45.0f,
            longitude = 10.0f
        )
        _selectedServer.value = doubleServer
        if (connectionStatus.value == ConnectionStatus.CONNECTED) {
            toggleConnection()
            toggleConnection()
        }
    }

    val securityLogs: StateFlow<List<String>> = com.example.vpn.VpnSecurityDiagnostics.diagnosticLogs

    fun toggleConnection() {
        val context = getApplication<Application>().applicationContext
        val server = _selectedServer.value ?: return

        when (connectionStatus.value) {
            ConnectionStatus.DISCONNECTED -> {
                ProtonVpnService.startVpn(
                    context = context,
                    serverName = "${server.cityName}, ${server.countryName}",
                    serverIp = server.endpointIp,
                    countryCode = server.countryCode,
                    countryName = server.countryName,
                    cityName = server.cityName,
                    killSwitch = _killSwitchEnabled.value,
                    splitTunneling = _splitTunnelingEnabled.value,
                    performanceMode = _performanceModeEnabled.value,
                    mtu = _mtuClamp.value,
                    protocol = _selectedProtocol.value.name
                )
            }
            ConnectionStatus.CONNECTED, ConnectionStatus.CONNECTING -> {
                ProtonVpnService.stopVpn(context, isUserInitiated = true)
            }
            ConnectionStatus.KILL_SWITCH_LOCKDOWN -> {
                ProtonVpnService.resetKillSwitch(context)
            }
            ConnectionStatus.DISCONNECTING -> {}
        }
    }

    fun setPerformanceMode(enabled: Boolean) {
        _performanceModeEnabled.value = enabled
        com.example.vpn.VpnSecurityDiagnostics.log(
            "TurboEngine",
            "Device Performance Mode: ${if (enabled) "ENABLED (Zero phone lag, 120Hz smooth UI, low CPU/battery)" else "DISABLED (Strict full encapsulation)"}."
        )
        if (connectionStatus.value == ConnectionStatus.CONNECTED) {
            val server = _selectedServer.value ?: return
            val context = getApplication<Application>().applicationContext
            ProtonVpnService.startVpn(
                context = context,
                serverName = "${server.cityName}, ${server.countryName}",
                serverIp = server.endpointIp,
                countryCode = server.countryCode,
                countryName = server.countryName,
                cityName = server.cityName,
                killSwitch = _killSwitchEnabled.value,
                splitTunneling = _splitTunnelingEnabled.value,
                performanceMode = enabled,
                mtu = _mtuClamp.value,
                protocol = _selectedProtocol.value.name
            )
        }
    }

    fun setKillSwitch(enabled: Boolean) {
        _killSwitchEnabled.value = enabled
        com.example.vpn.VpnSecurityDiagnostics.log(
            "KillSwitch",
            "Kill switch set to: ${if (enabled) "ENABLED (Fail-Closed)" else "DISABLED"}."
        )
    }

    fun setProtocol(protocol: VpnProtocol) {
        _selectedProtocol.value = protocol
        com.example.vpn.VpnSecurityDiagnostics.log(
            "Protocol",
            "Tunnel protocol selected: ${protocol.displayName}."
        )
    }

    fun toggleNetShield(enabled: Boolean) {
        ProtonVpnService.setNetShield(enabled)
    }

    fun toggleAutoConnectWifi(enabled: Boolean) {
        _autoConnectWifi.value = enabled
        com.example.vpn.VpnSecurityDiagnostics.log(
            "WiFiGuard",
            if (enabled) "Auto-Connect on Untrusted Wi-Fi: ACTIVATED. Public hotspots will be automatically secured."
            else "Auto-Connect on Untrusted Wi-Fi: DEACTIVATED."
        )
    }

    fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024L -> "$bytes B"
            bytes < 1024L * 1024L -> String.format(java.util.Locale.US, "%.1f KB", bytes / 1024.0)
            bytes < 1024L * 1024L * 1024L -> String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format(java.util.Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    fun setMtuClamp(mtu: Int) {
        _mtuClamp.value = mtu
        com.example.vpn.VpnSecurityDiagnostics.log(
            "MTU",
            "MTU clamp adjusted to: $mtu bytes."
        )
    }

    fun setSplitTunneling(enabled: Boolean) {
        _splitTunnelingEnabled.value = enabled
        com.example.vpn.VpnSecurityDiagnostics.log(
            "SplitTunnel",
            "Split tunneling changed: ${if (enabled) "Active (LAN exempted, Chrome protected)" else "Disabled (100% full tunnel)"}."
        )
    }

    fun runLeakTest() {
        viewModelScope.launch {
            _isTestingLeak.value = true
            val currentServer = _selectedServer.value
            val activeIp = currentServer?.endpointIp ?: "185.120.44.12"
            val countryName = currentServer?.countryName ?: "Germany"
            val countryCode = currentServer?.countryCode ?: "DE"
            val cityName = currentServer?.cityName ?: "Frankfurt"
            val isConnected = connectionStatus.value == ConnectionStatus.CONNECTED
            val result = repository.runDiagnosticLeakTest(
                activeServerIp = activeIp,
                countryName = countryName,
                countryCode = countryCode,
                cityName = cityName,
                isConnected = isConnected,
                isKillSwitchActive = _killSwitchEnabled.value,
                isSplitTunnelActive = _splitTunnelingEnabled.value
            )
            _leakTestResult.value = result
            _isTestingLeak.value = false
        }
    }

    fun connectToDedicatedIp() {
        val dedicatedServer = allServers.value.find { it.supportsDedicatedIp } ?: return
        selectServer(dedicatedServer)
        if (connectionStatus.value != ConnectionStatus.CONNECTED) {
            toggleConnection()
        }
    }

    fun selectPoolCountry(countryCode: String) {
        _selectedPoolCountryCode.value = countryCode
    }

    fun setStaticIpSearchQuery(query: String) {
        _staticIpSearchQuery.value = query
    }

    fun setStaticIpTagFilter(tag: String) {
        _staticIpTagFilter.value = tag
    }

    fun connectToStaticIp(staticIp: StaticIpAddress) {
        _assignedStaticIp.value = staticIp
        _selectedPoolCountryCode.value = staticIp.countryCode

        val staticServer = ServerEntity(
            id = staticIp.id,
            countryCode = staticIp.countryCode,
            countryName = staticIp.countryName,
            flagEmoji = staticIp.flagEmoji,
            cityName = "${staticIp.cityName} [Static IP]",
            hostname = staticIp.hostname,
            endpointIp = staticIp.ip,
            port = staticIp.port,
            publicKey = staticIp.publicKey,
            latencyMs = staticIp.latencyMs,
            loadPercent = staticIp.loadPercent,
            packetLossPercent = 0.0f,
            capacityMbps = 10000,
            supportsDedicatedIp = true,
            isFavorite = false
        )

        selectServer(staticServer)

        com.example.vpn.VpnSecurityDiagnostics.log(
            "StaticPool",
            "Static IP Allocated: ${staticIp.ip} (${staticIp.cityName}, ${staticIp.countryName}) | Type: ${staticIp.ipType} | Tag: ${staticIp.tag} | 100% Clean Reputation."
        )

        if (connectionStatus.value != ConnectionStatus.CONNECTED) {
            toggleConnection()
        }
    }

    fun releaseStaticIp() {
        _assignedStaticIp.value = null
        com.example.vpn.VpnSecurityDiagnostics.log(
            "StaticPool",
            "Static IP released back to pool."
        )
    }
}
