package com.example

import com.example.data.repository.StaticIpPoolManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGermanyStaticIpPoolHas20CleanIps() {
    val pool = StaticIpPoolManager.getCountryIpPool("DE", "Germany", "🇩🇪")
    assertEquals("DE", pool.countryCode)
    assertEquals(20, pool.totalIps)
    assertEquals(20, pool.ips.size)
    assertEquals(100, pool.cleanReputationPercent)

    // Verify all 20 IPs are unique and have 0% abuse score
    val uniqueIps = pool.ips.map { it.ip }.toSet()
    assertEquals(20, uniqueIps.size)
    assertTrue(pool.ips.all { it.abuseScore == 0.0f })
    assertTrue(pool.ips.all { it.reputationScore == 100 })
    assertTrue(pool.ips.all { it.ip.startsWith("185.120.44.") })
  }

  @Test
  fun testRequiredCountriesEachHave20StaticIps() {
    val testCountries = listOf(
      Triple("DE", "Germany", "🇩🇪"),
      Triple("NL", "Netherlands", "🇳🇱"),
      Triple("GB", "United Kingdom", "🇬🇧"),
      Triple("US", "United States", "🇺🇸"),
      Triple("JP", "Japan", "🇯🇵"),
      Triple("SG", "Singapore", "🇸🇬")
    )

    for ((code, name, flag) in testCountries) {
      val pool = StaticIpPoolManager.getCountryIpPool(code, name, flag)
      assertEquals("Country $name must have 20 IPs", 20, pool.ips.size)
      val uniqueIps = pool.ips.map { it.ip }.toSet()
      assertEquals("Country $name must have 20 unique IPs", 20, uniqueIps.size)
      assertTrue("Country $name IPs must all have clean reputation", pool.ips.all { it.reputationScore == 100 })
      assertTrue("Country $name IPs must have 0% abuse score", pool.ips.all { it.abuseScore == 0.0f })
    }
  }
}
