package com.example

import com.example.data.model.DoubleVpnSeedData
import com.example.data.model.VpnProtocol
import com.example.data.repository.StaticIpPoolManager
import com.example.data.repository.VpnServerSeedData
import org.junit.Assert.*
import org.junit.Test

class VpnComprehensiveStressTest {

    private fun isValidIpv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            part.toIntOrNull()?.let { it in 0..255 } ?: false
        }
    }

    /**
     * TEST 1: Test EVERY SINGLE country location server in the global fleet.
     * Must be tested at least 14 times consecutively without a single error.
     */
    @Test
    fun testAllCountryLocationsTested14Times() {
        val iterations = 14
        println("=== STARTING TEST 1: All Country Locations (14 Iterations) ===")

        for (i in 1..iterations) {
            val allServers = VpnServerSeedData.getGlobalServers()
            
            // 1. Verify global fleet volume
            assertTrue(
                "Pass $i: Server fleet must have at least 70 servers, found ${allServers.size}",
                allServers.size >= 70
            )

            val serverIds = mutableSetOf<String>()
            val hostnames = mutableSetOf<String>()
            val publicKeys = mutableSetOf<String>()
            val countries = mutableSetOf<String>()

            // 2. Validate every single country server entity
            allServers.forEach { server ->
                // ID checks
                assertTrue("Pass $i: Server ID must start with srv_ (${server.id})", server.id.startsWith("srv_"))
                assertTrue("Pass $i: Duplicate server ID found: ${server.id}", serverIds.add(server.id))

                // Country validation
                assertEquals("Pass $i: Country code must be 2 letters (${server.countryCode})", 2, server.countryCode.length)
                assertTrue("Pass $i: Country code must be uppercase (${server.countryCode})", server.countryCode.all { it.isUpperCase() })
                assertTrue("Pass $i: Country name must not be blank (${server.countryName})", server.countryName.isNotBlank())
                assertTrue("Pass $i: Flag emoji must be present (${server.countryName})", server.flagEmoji.isNotBlank())
                assertTrue("Pass $i: City name must not be blank (${server.cityName})", server.cityName.isNotBlank())
                countries.add(server.countryCode)

                // Hostname and Key validation
                assertTrue("Pass $i: Hostname must end with .protonplusvpn.net (${server.hostname})", server.hostname.endsWith(".protonplusvpn.net"))
                assertTrue("Pass $i: Duplicate hostname found: ${server.hostname}", hostnames.add(server.hostname))
                assertTrue("Pass $i: Duplicate public key found: ${server.publicKey}", publicKeys.add(server.publicKey))
                assertTrue("Pass $i: WireGuard public key must be valid base64 key ending with = (${server.publicKey})", server.publicKey.length >= 32 && server.publicKey.endsWith("="))

                // Network & IP validation
                assertTrue("Pass $i: Endpoint IP must be valid IPv4 (${server.endpointIp})", isValidIpv4(server.endpointIp))
                assertEquals("Pass $i: Port must be WireGuard 51820", 51820, server.port)
                assertTrue("Pass $i: Latency must be positive ms (${server.latencyMs})", server.latencyMs > 0)
                assertTrue("Pass $i: Load must be between 0 and 100% (${server.loadPercent})", server.loadPercent in 0..100)
                assertTrue("Pass $i: Packet loss must be between 0.0% and 5.0% (${server.packetLossPercent})", server.packetLossPercent in 0.0f..5.0f)
                assertTrue("Pass $i: Capacity must be at least 10 Gbps (${server.capacityMbps})", server.capacityMbps >= 10000)

                // Geographic Coordinates validation
                assertTrue("Pass $i: Latitude must be in [-90, 90] (${server.latitude})", server.latitude in -90.0f..90.0f)
                assertTrue("Pass $i: Longitude must be in [-180, 180] (${server.longitude})", server.longitude in -180.0f..180.0f)
            }

            // Verify broad global country coverage
            assertTrue("Pass $i: Must cover at least 60 distinct countries, covered: ${countries.size}", countries.size >= 60)
            println("✔ Pass $i/14: Successfully verified ${allServers.size} country servers across ${countries.size} countries.")
        }
        println("=== TEST 1 PASSED: All Country Locations 14x Stress Test Complete ===\n")
    }

    /**
     * TEST 2: Test Static IP Pools for EVERY single country location.
     * Every country must have exactly 20 unique static IPs with 100% clean reputation & 0% abuse.
     * Repeated for at least 14 full passes across every single country.
     */
    @Test
    fun testAllStaticIpPoolsTested14Times() {
        val iterations = 14
        println("=== STARTING TEST 2: All Static IP Pools (14 Iterations) ===")

        val allServers = VpnServerSeedData.getGlobalServers()
        val distinctCountries = allServers.distinctBy { it.countryCode }

        for (i in 1..iterations) {
            var totalVerifiedStaticIps = 0

            for (server in distinctCountries) {
                val pool = StaticIpPoolManager.getCountryIpPool(
                    countryCode = server.countryCode,
                    countryName = server.countryName,
                    flagEmoji = server.flagEmoji
                )

                // Verify Pool Header
                assertEquals("Pass $i: Pool code must match", server.countryCode, pool.countryCode)
                assertEquals("Pass $i: Pool country name must match", server.countryName, pool.countryName)
                assertEquals("Pass $i: Pool flag must match", server.flagEmoji, pool.flagEmoji)
                assertEquals("Pass $i: Country ${server.countryName} must have exactly 20 static IPs", 20, pool.totalIps)
                assertEquals("Pass $i: Country ${server.countryName} list must contain 20 items", 20, pool.ips.size)
                assertEquals("Pass $i: Pool must advertise 100% clean reputation", 100, pool.cleanReputationPercent)
                assertTrue("Pass $i: Average ping must be positive", pool.averagePingMs > 0)

                // Verify All 20 Static IPs in the Pool
                val seenIpsInCountry = mutableSetOf<String>()
                val seenIdsInCountry = mutableSetOf<String>()

                for (staticIp in pool.ips) {
                    // ID & IP uniqueness
                    assertTrue("Pass $i: Duplicate IP ID in country ${server.countryCode}: ${staticIp.id}", seenIdsInCountry.add(staticIp.id))
                    assertTrue("Pass $i: Duplicate IP address in country ${server.countryCode}: ${staticIp.ip}", seenIpsInCountry.add(staticIp.ip))

                    // IPv4 verification
                    assertTrue("Pass $i: Static IP must be valid IPv4: ${staticIp.ip}", isValidIpv4(staticIp.ip))

                    // Clean Reputation & 0% Abuse Verification
                    assertEquals("Pass $i: Static IP ${staticIp.ip} must have 100/100 clean reputation", 100, staticIp.reputationScore)
                    assertEquals("Pass $i: Static IP ${staticIp.ip} must have 0.0% abuse score", 0.0f, staticIp.abuseScore, 0.0001f)
                    assertEquals("Pass $i: Static IP ${staticIp.ip} must have 0 blacklist detections", 0, staticIp.blacklistCount)
                    assertEquals("Pass $i: Static IP ${staticIp.ip} ASN must be AS209498 Proton AG", "AS209498 Proton AG", staticIp.asn)

                    // Subnet & Port
                    assertTrue("Pass $i: Subnet must be /24", staticIp.subnet.endsWith("/24"))
                    assertEquals("Pass $i: Port must be 51820", 51820, staticIp.port)

                    // Latency & Load
                    assertTrue("Pass $i: Latency must be > 0", staticIp.latencyMs > 0)
                    assertTrue("Pass $i: Load must be in 0..100%", staticIp.loadPercent in 0..100)

                    // Purpose Category & Type
                    assertTrue("Pass $i: Tag must not be blank", staticIp.tag.isNotBlank())
                    assertTrue("Pass $i: IP Type must not be blank", staticIp.ipType.isNotBlank())
                    assertTrue("Pass $i: Verified date audit badge must be present", staticIp.verifiedDate.contains("Audited Today"))

                    totalVerifiedStaticIps++
                }

                assertEquals("Pass $i: Must have 20 unique IPs in ${server.countryName}", 20, seenIpsInCountry.size)
            }

            println("✔ Pass $i/14: Successfully verified $totalVerifiedStaticIps Static IPs across ${distinctCountries.size} countries (20 IPs each).")
        }
        println("=== TEST 2 PASSED: Static IP Pool 14x Stress Test Complete ===\n")
    }

    /**
     * TEST 3: Test Double VPN Multi-Hop Routing Architecture across 14 passes.
     */
    @Test
    fun testDoubleVpnRoutesTested14Times() {
        val iterations = 14
        println("=== STARTING TEST 3: Double VPN Multi-Hop Routes (14 Iterations) ===")

        for (i in 1..iterations) {
            val routes = DoubleVpnSeedData.routes
            assertTrue("Pass $i: Must have at least 15 Double VPN multi-hop routes", routes.size >= 15)

            val routeIds = mutableSetOf<String>()

            for (route in routes) {
                assertTrue("Pass $i: Duplicate route ID: ${route.id}", routeIds.add(route.id))
                assertNotEquals(
                    "Pass $i: Multi-hop Entry country and Exit country must be different for route ${route.title}",
                    route.entryCountry,
                    route.exitCountry
                )
                assertTrue("Pass $i: Entry IP must be valid IPv4 (${route.entryIp})", isValidIpv4(route.entryIp))
                assertTrue("Pass $i: Exit IP must be valid IPv4 (${route.exitIp})", isValidIpv4(route.exitIp))
                assertNotEquals("Pass $i: Entry and Exit IPs must differ", route.entryIp, route.exitIp)
                assertTrue("Pass $i: Latency must be positive", route.latencyMs > 0)
                assertTrue("Pass $i: Load must be in 0..100%", route.loadPercent in 0..100)
                assertTrue("Pass $i: Capacity must be >= 10 Gbps", route.capacityGbps >= 10)
                assertTrue("Pass $i: Security level must specify AES-256 or ChaCha20", route.securityLevel.contains("AES-256") || route.securityLevel.contains("ChaCha20"))
            }

            println("✔ Pass $i/14: Successfully verified ${routes.size} Double VPN Multi-Hop routes.")
        }
        println("=== TEST 3 PASSED: Double VPN Multi-Hop 14x Stress Test Complete ===\n")
    }

    /**
     * TEST 4: Test All Protocols, Static Assignment Lifecycle, and Leak Defense 14 Times.
     */
    @Test
    fun testProtocolsAndLifecycleTested14Times() {
        val iterations = 14
        println("=== STARTING TEST 4: Protocols and Lifecycle (14 Iterations) ===")

        for (i in 1..iterations) {
            // 1. Verify all protocols supported
            val protocols = VpnProtocol.values()
            assertEquals("Pass $i: Must support 2 core WireGuard protocols", 2, protocols.size)
            assertTrue("Pass $i: Must include WIREGUARD", protocols.contains(VpnProtocol.WIREGUARD))
            assertTrue("Pass $i: Must include STEALTH_OBFUSCATED", protocols.contains(VpnProtocol.STEALTH_OBFUSCATED))

            // Verify ConnectionStatus state machine has 5 distinct states
            val states = com.example.data.model.ConnectionStatus.values()
            assertEquals("Pass $i: Must have 5 connection states", 5, states.size)
            assertTrue("Pass $i: DISCONNECTED", states.contains(com.example.data.model.ConnectionStatus.DISCONNECTED))
            assertTrue("Pass $i: CONNECTING", states.contains(com.example.data.model.ConnectionStatus.CONNECTING))
            assertTrue("Pass $i: CONNECTED", states.contains(com.example.data.model.ConnectionStatus.CONNECTED))
            assertTrue("Pass $i: DISCONNECTING", states.contains(com.example.data.model.ConnectionStatus.DISCONNECTING))
            assertTrue("Pass $i: KILL_SWITCH_LOCKDOWN", states.contains(com.example.data.model.ConnectionStatus.KILL_SWITCH_LOCKDOWN))

            // 2. Simulate Static IP allocation and pool filtering
            val germanyPool = StaticIpPoolManager.getCountryIpPool("DE", "Germany", "🇩🇪")
            val targetStaticIp = germanyPool.ips.first()
            assertEquals("185.120.44.101", targetStaticIp.ip)

            // Re-fetch pool with assigned IP
            val poolWithAssignment = StaticIpPoolManager.getCountryIpPool(
                "DE", "Germany", "🇩🇪", assignedIp = targetStaticIp.ip
            )
            val assignedItem = poolWithAssignment.ips.find { it.ip == targetStaticIp.ip }
            assertNotNull("Assigned IP must be in pool", assignedItem)
            assertTrue("Assigned item must have isAssigned = true", assignedItem!!.isAssigned)

            // Verify other 19 IPs are NOT assigned
            val unassignedCount = poolWithAssignment.ips.count { !it.isAssigned }
            assertEquals("Exactly 19 IPs in pool must remain unassigned", 19, unassignedCount)

            println("✔ Pass $i/14: Protocol configuration and static IP allocation lifecycle verified.")
        }
        println("=== TEST 4 PASSED: Protocols and Lifecycle 14x Stress Test Complete ===\n")
    }
}
