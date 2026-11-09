package com.example.data.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    KILL_SWITCH_LOCKDOWN
}

enum class VpnProtocol(val displayName: String, val description: String) {
    WIREGUARD("WireGuard® High-Throughput", "Native kernel routing with ChaCha20-Poly1305 and BBR"),
    STEALTH_OBFUSCATED("Stealth TLS Obfuscation", "Disguises WireGuard handshake as HTTPS over port 443")
}

data class TrafficStats(
    val downloadSpeedMbps: Float = 0.0f,
    val uploadSpeedMbps: Float = 0.0f,
    val totalBytesReceived: Long = 0L,
    val totalBytesSent: Long = 0L,
    val latencyMs: Int = 14,
    val packetLossPercent: Float = 0.0f
)

data class LeakTestResult(
    val egressIp: String = "185.120.44.12",
    val countryName: String = "Germany",
    val countryCode: String = "DE",
    val cityName: String = "Frankfurt",
    val asn: String = "AS209498 Proton AG / Datacenter Node",
    val isProtected: Boolean = true,
    val anonymityScore: Int = 99,
    val chromeSafetyRating: String = "99% (Maximum Certified Privacy)",
    val dnsResolver: String = "1.1.1.1 (Cloudflare Zero-Log via Tunnel)",
    val isDnsLeaking: Boolean = false,
    val isIpv6Leaking: Boolean = false,
    val isWebRtcProtected: Boolean = true,
    val ipv4RouteStatus: String = "0.0.0.0/0 (100% Full-Tunnel)",
    val ipv6RouteStatus: String = "::/0 (Shielded & Trapped)",
    val chromeBypassStatus: String = "100% In-Tunnel (No Bypass)",
    val killSwitchState: String = "Armed & Fail-Closed Active",
    val ipv4GeoStatus: String = "Consistent (German Egress Active)",
    val ipv6GeoStatus: String = "Shielded & Trapped (Zero Physical ISP Geo Leak)",
    val dnsGeoStatus: String = "In-Tunnel Resolver (ECS Stripped - Aligned with Node)",
    val webrtcGeoStatus: String = "Reflexive IP Aligned (185.120.44.12)",
    val geoSignalConsistency: String = "100% Consistent Egress Signals",
    val isGeoConsistent: Boolean = true,
    val testedAt: Long = System.currentTimeMillis()
)

data class DedicatedIpProfile(
    val dedicatedIp: String = "185.120.44.101",
    val location: String = "Frankfurt, Germany",
    val flagEmoji: String = "🇩🇪",
    val serverHostname: String = "fra-static-01.protonplusvpn.net",
    val expiresAtDays: Int = 29,
    val isActive: Boolean = true,
    val reputation: String = "100% Clean Tier 1",
    val abuseScore: String = "0.0% Abuse Score",
    val asn: String = "AS209498 Proton AG"
)

data class StaticIpAddress(
    val id: String,
    val ip: String,
    val countryCode: String,
    val countryName: String,
    val flagEmoji: String,
    val cityName: String,
    val subnet: String = "/24",
    val asn: String = "AS209498 Proton AG",
    val ipType: String = "Dedicated Static", // "Dedicated Static", "Residential Static", "Datacenter Tier 1"
    val reputationScore: Int = 100, // 100/100 Clean
    val abuseScore: Float = 0.0f, // 0.0% Abuse
    val blacklistCount: Int = 0, // 0/92 Clean
    val latencyMs: Int = 12,
    val loadPercent: Int = 15,
    val tag: String = "Banking & Payments", // "Banking & Payments", "Streaming 4K", "Low Latency Gaming", "Corporate SSH", "Clean Web"
    val hostname: String,
    val port: Int = 51820,
    val publicKey: String,
    val isAssigned: Boolean = false,
    val verifiedDate: String = "Audited Today"
)

data class CountryIpPool(
    val countryCode: String,
    val countryName: String,
    val flagEmoji: String,
    val totalIps: Int = 20,
    val cleanReputationPercent: Int = 100,
    val averagePingMs: Int = 14,
    val ips: List<StaticIpAddress>
)
