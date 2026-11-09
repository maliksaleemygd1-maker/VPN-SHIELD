#!/usr/bin/env bash
# ==============================================================================
# Proton Plus VPN - Dynamic Path MTU (PMTU) & MSS Clamping Optimizer
# ==============================================================================
set -euo pipefail

TARGET_HOST="${1:-1.1.1.1}"
echo "[*] Discovering optimal Path MTU towards $TARGET_HOST..."

FOUND_MTU=1500
# Test common MTU boundaries: 1500 (Ethernet), 1492 (PPPoE), 1420 (WireGuard), 1280 (IPv6 Min)
for test_mtu in 1500 1492 1420 1380 1280; do
    PAYLOAD_SIZE=$((test_mtu - 28)) # Subtract IP(20) + ICMP(8)
    if ping -c 1 -M do -s "$PAYLOAD_SIZE" -W 1 "$TARGET_HOST" >/dev/null 2>&1; then
        FOUND_MTU=$test_mtu
        break
    fi
done

echo "[+] Path MTU discovered: $FOUND_MTU bytes"

# Calculate WireGuard recommended client MTU (Subtract 80 bytes for IPv6 WireGuard encapsulation)
WG_OPTIMAL_MTU=$((FOUND_MTU - 80))
if [ "$WG_OPTIMAL_MTU" -gt 1420 ]; then
    WG_OPTIMAL_MTU=1420
fi

echo "[+] Recommended WireGuard MTU: $WG_OPTIMAL_MTU bytes"
echo "[+] Recommended TCP MSS Clamp: $((WG_OPTIMAL_MTU - 40)) bytes"
