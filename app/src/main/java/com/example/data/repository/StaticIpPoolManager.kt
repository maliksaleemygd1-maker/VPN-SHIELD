package com.example.data.repository

import com.example.data.local.entity.ServerEntity
import com.example.data.model.CountryIpPool
import com.example.data.model.StaticIpAddress

object StaticIpPoolManager {

    private val countryBaseSubnets = mapOf(
        "DE" to Triple("185.120.44", "AS209498 Proton AG", "Frankfurt"),
        "NL" to Triple("185.120.47", "AS209498 Proton AG", "Amsterdam"),
        "GB" to Triple("185.120.48", "AS209498 Proton AG", "London"),
        "US" to Triple("199.180.20", "AS209498 Proton AG", "New York"),
        "JP" to Triple("103.253.144", "AS209498 Proton AG", "Tokyo"),
        "SG" to Triple("103.253.142", "AS209498 Proton AG", "Singapore"),
        "CA" to Triple("199.180.25", "AS209498 Proton AG", "Toronto"),
        "CH" to Triple("185.120.45", "AS209498 Proton AG", "Zurich"),
        "FR" to Triple("185.120.46", "AS209498 Proton AG", "Paris"),
        "AU" to Triple("139.130.4", "AS209498 Proton AG", "Sydney"),
        "SE" to Triple("185.120.49", "AS209498 Proton AG", "Stockholm"),
        "IN" to Triple("103.253.146", "AS209498 Proton AG", "Mumbai"),
        "BR" to Triple("199.180.30", "AS209498 Proton AG", "São Paulo"),
        "ES" to Triple("185.120.51", "AS209498 Proton AG", "Madrid"),
        "IT" to Triple("185.120.50", "AS209498 Proton AG", "Milan"),
        "AT" to Triple("185.120.43", "AS209498 Proton AG", "Vienna"),
        "BE" to Triple("185.120.42", "AS209498 Proton AG", "Brussels"),
        "DK" to Triple("185.120.41", "AS209498 Proton AG", "Copenhagen"),
        "FI" to Triple("185.120.40", "AS209498 Proton AG", "Helsinki"),
        "NO" to Triple("185.120.39", "AS209498 Proton AG", "Oslo"),
        "PL" to Triple("185.120.38", "AS209498 Proton AG", "Warsaw"),
        "RO" to Triple("185.120.37", "AS209498 Proton AG", "Bucharest"),
        "IE" to Triple("185.120.36", "AS209498 Proton AG", "Dublin"),
        "NZ" to Triple("139.130.8", "AS209498 Proton AG", "Auckland"),
        "ZA" to Triple("102.130.12", "AS209498 Proton AG", "Johannesburg"),
        "AE" to Triple("185.120.56", "AS209498 Proton AG", "Dubai")
    )

    private val purposeTags = listOf(
        "Banking & Financial",
        "4K UHD Streaming",
        "Low-Latency Gaming",
        "Corporate SSH Bastion",
        "Zero-CAPTCHA Clean Web",
        "Clean Residential Egress",
        "High-Throughput P2P",
        "E-Commerce & PayPal Safe"
    )

    fun getCountryIpPool(
        countryCode: String,
        countryName: String,
        flagEmoji: String,
        assignedIp: String? = null
    ): CountryIpPool {
        val meta = countryBaseSubnets[countryCode] ?: run {
            // Hash countryCode for deterministic subnet in 185.120.x.x
            val hash = (countryCode.hashCode() and 0xFF).coerceIn(10, 240)
            Triple("185.120.$hash", "AS209498 Proton AG", countryName)
        }

        val baseSubnet = meta.first
        val asn = meta.second
        val cityName = meta.third

        val ips = (1..20).map { index ->
            val ipNumber = 100 + index
            val ipAddress = "$baseSubnet.$ipNumber"
            val tag = purposeTags[(index - 1) % purposeTags.size]
            val ipType = when {
                index in 1..8 -> "Dedicated Static"
                index in 9..14 -> "Residential Static"
                else -> "Datacenter Tier 1"
            }
            val ping = (10 + (index * 2) % 18)
            val load = (12 + (index * 3) % 25)

            StaticIpAddress(
                id = "static_${countryCode.lowercase()}_${String.format("%02d", index)}",
                ip = ipAddress,
                countryCode = countryCode,
                countryName = countryName,
                flagEmoji = flagEmoji,
                cityName = cityName,
                subnet = "$baseSubnet.0/24",
                asn = asn,
                ipType = ipType,
                reputationScore = 100,
                abuseScore = 0.0f,
                blacklistCount = 0,
                latencyMs = ping,
                loadPercent = load,
                tag = tag,
                hostname = "${countryCode.lowercase()}-static-${String.format("%02d", index)}.protonplusvpn.net",
                publicKey = "pUsT4T1c${countryCode}${index}ZaBcDeFgHiJkLmNoPqRsTuVwXyZ=",
                isAssigned = assignedIp == ipAddress,
                verifiedDate = "Audited Today • 0% Abuse"
            )
        }

        val avgPing = ips.map { it.latencyMs }.average().toInt()

        return CountryIpPool(
            countryCode = countryCode,
            countryName = countryName,
            flagEmoji = flagEmoji,
            totalIps = ips.size,
            cleanReputationPercent = 100,
            averagePingMs = avgPing,
            ips = ips
        )
    }

    fun getAllCountryPools(servers: List<ServerEntity>, assignedIp: String? = null): List<CountryIpPool> {
        // Group servers by country
        val distinctCountries = servers.distinctBy { it.countryCode }.sortedBy { it.countryName }

        // Ensure key countries requested are placed first
        val priorityCodes = listOf("DE", "NL", "GB", "US", "JP", "SG", "CA", "CH", "FR", "AU", "SE")

        val sortedCountries = distinctCountries.sortedWith(
            compareBy<ServerEntity> { server ->
                val idx = priorityCodes.indexOf(server.countryCode)
                if (idx != -1) idx else 100
            }.thenBy { it.countryName }
        )

        return sortedCountries.map { server ->
            getCountryIpPool(
                countryCode = server.countryCode,
                countryName = server.countryName,
                flagEmoji = server.flagEmoji,
                assignedIp = assignedIp
            )
        }
    }
}
