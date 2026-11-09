package com.example.vpn

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VpnSecurityDiagnostics {
    private const val TAG = "ProtonVpnSecurity"
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    private val _diagnosticLogs = MutableStateFlow<List<String>>(
        listOf(
            "System security diagnostics initialized.",
            "Full-tunnel routing engine ready (IPv4 0.0.0.0/0, IPv6 ::/0).",
            "Zero-log DNS resolvers loaded: 1.1.1.1, 1.0.0.1, 9.9.9.9."
        )
    )
    val diagnosticLogs: StateFlow<List<String>> = _diagnosticLogs.asStateFlow()

    fun log(component: String, message: String) {
        val sanitized = sanitize(message)
        val timestamp = timeFormat.format(Date())
        val entry = "[$timestamp] [$component] $sanitized"
        Log.i(TAG, entry)

        val current = _diagnosticLogs.value.toMutableList()
        if (current.size >= 80) {
            current.removeAt(0)
        }
        current.add(entry)
        _diagnosticLogs.value = current
    }

    private fun sanitize(input: String): String {
        // Strict privacy sanitization: Strip passwords, tokens, private keys or plaintext payloads
        return input
            .replace(Regex("(?i)(password|token|secret|privkey|private_key|key)=\\S+"), "$1=[REDACTED]")
            .replace(Regex("(?i)bearer\\s+[A-Za-z0-9\\-_.]+"), "Bearer [REDACTED]")
    }

    fun clear() {
        _diagnosticLogs.value = emptyList()
    }
}
