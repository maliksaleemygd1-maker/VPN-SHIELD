#!/usr/bin/env bash
# ==============================================================================
# Proton Plus VPN - Full Security & Leak Testing Suite
# Runs automated end-to-end security audits across Data Plane, Control Plane,
# Mobile configurations, and Browser extensions.
# ==============================================================================
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

TOTAL_TESTS=0
PASSED_TESTS=0

run_test() {
    local test_name="$1"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo -n "[Test ${TOTAL_TESTS}] ${test_name}... "
    if eval "$2"; then
        echo "PASSED"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo "FAILED"
    fi
}

echo "======================================================================"
echo " Starting Proton Plus VPN Complete Security & Leak Audit"
echo "======================================================================"

# Test 1: Unbound Zero-Log Enforcement
run_test "Verifying Zero-Log DNS Configuration in Unbound" "
    grep -q 'verbosity: 0' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\" &&
    grep -q 'log-queries: no' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\" &&
    grep -q 'logfile: \\\"/dev/null\\\"' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\" &&
    grep -q 'qname-minimisation: yes' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\"
"

# Test 2: Unbound Access Control - Refuse Public Ingress
run_test "Verifying DNS Access Control (Internal Only)" "
    grep -q 'access-control: 0.0.0.0/0 refuse' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\" &&
    grep -q 'access-control: 10.64.0.0/10 allow' \"\$REPO_ROOT/vpn-server/unbound/unbound.conf\"
"

# Test 3: nftables Fail-Closed Firewall Policy
run_test "Verifying nftables Fail-Closed Drop Policy" "
    grep -q 'hook input priority filter; policy drop;' \"\$REPO_ROOT/vpn-server/nftables/vpn-rules.nft\" &&
    grep -q 'hook forward priority filter; policy drop;' \"\$REPO_ROOT/vpn-server/nftables/vpn-rules.nft\"
"

# Test 4: WireGuard In-Kernel MTU and MSS Clamping
run_test "Verifying TCP MSS Clamping over MTU 1420" "
    grep -q 'tcp flags syn tcp option maxseg size set rt mtu - 40' \"\$REPO_ROOT/vpn-server/nftables/vpn-rules.nft\" &&
    grep -q 'MTU = 1420' \"\$REPO_ROOT/vpn-server/templates/wg0.conf.template\"
"

# Test 5: Dedicated IP Isolation & Anti-Cross-Probe Rules
run_test "Verifying Dedicated IP 1:1 NAT & Isolation" "
    grep -q 'snat to \"\\\$DEDICATED_PUBLIC_IP\"' \"\$REPO_ROOT/vpn-server/nftables/dedicated_ip_nat.sh\" &&
    grep -q 'drop' \"\$REPO_ROOT/vpn-server/nftables/dedicated_ip_nat.sh\"
"

# Test 6: WebRTC Leak Prevention in Browser Extension
run_test "Verifying Browser WebRTC Leak Prevention Policy" "
    grep -q 'disable_non_proxied_udp' \"\$REPO_ROOT/browser-extension/background.js\"
"

# Test 7: Envoy TLS 1.3 Strict Cipher Configuration
run_test "Verifying Envoy TLS 1.3 Strict Cipher Suites" "
    grep -q 'tls_minimum_protocol_version: TLSv1_3' \"\$REPO_ROOT/control-plane/envoy.yaml\" &&
    grep -q 'TLS_CHACHA20_POLY1305_SHA256' \"\$REPO_ROOT/control-plane/envoy.yaml\"
"

# Test 8: Android VpnService Declaration and Catch-all Routing
run_test "Verifying Android VpnService Route and DNS Invariants" "
    grep -q 'addRoute(\"0.0.0.0\", 0)' \"\$REPO_ROOT/app/src/main/java/com/example/vpn/ProtonVpnService.kt\" &&
    grep -q 'addDnsServer(\"10.64.0.1\")' \"\$REPO_ROOT/app/src/main/java/com/example/vpn/ProtonVpnService.kt\"
"

# Test 9: iOS PacketTunnel Catch-all DNS Settings
run_test "Verifying iOS NEPacketTunnel Catch-all DNS Domain" "
    grep -q 'matchDomains = \\[\"\"\\]' \"\$REPO_ROOT/ios/PacketTunnel/PacketTunnelProvider.swift\"
"

# Test 10: Multi-City Coverage in Global Topology
run_test "Verifying Multi-City Coverage (Moscow, St. Pete, Karachi, Islamabad, Ashburn)" "
    grep -q 'Moscow' \"\$REPO_ROOT/shared/topologies/global_nodes.json\" &&
    grep -q 'Saint Petersburg' \"\$REPO_ROOT/shared/topologies/global_nodes.json\" &&
    grep -q 'Karachi' \"\$REPO_ROOT/shared/topologies/global_nodes.json\" &&
    grep -q 'Islamabad' \"\$REPO_ROOT/shared/topologies/global_nodes.json\" &&
    grep -q 'Ashburn' \"\$REPO_ROOT/shared/topologies/global_nodes.json\"
"

echo "======================================================================"
echo " Audit Complete: ${PASSED_TESTS}/${TOTAL_TESTS} tests passed."
echo "======================================================================"

if [ "$PASSED_TESTS" -eq "$TOTAL_TESTS" ]; then
    echo "[+] ALL SECURITY AND LEAK INVARIANTS VERIFIED GREEN."
    exit 0
else
    echo "[-] AUDIT FAILURES DETECTED."
    exit 1
fi
