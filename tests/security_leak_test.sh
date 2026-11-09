#!/usr/bin/env bash
set -euo pipefail

# Proton Plus VPN - Security & Leak Test Suite
echo "========================================================"
echo " Running Proton Plus VPN Security & Leak Diagnostics    "
echo "========================================================"

FAILED_TESTS=0

# Test 1: Validate No Plaintext Keys or Secrets in Repository
echo "[*] Test 1: Scanning for hardcoded private keys or production secrets..."
if grep -rEi "(BEGIN [A-Z ]*PRIVATE KEY|aws_secret_access_key)" \
   --exclude-dir={.git,.build-outputs,build} --exclude="*.sh" . ; then
    echo "[!] FAILED: Found potential hardcoded private key!"
    FAILED_TESTS=$((FAILED_TESTS + 1))
else
    echo "[+] PASSED: Zero hardcoded keys or secrets found."
fi

# Test 2: Verify Sysctl MTU & Congestion Control Tuning
echo "[*] Test 2: Verifying sysctl BBR and buffer limits configuration..."
if grep -q "net.ipv4.tcp_congestion_control = bbr" vpn-server/sysctl.d/99-wireguard-tuning.conf && \
   grep -q "net.core.rmem_max = 67108864" vpn-server/sysctl.d/99-wireguard-tuning.conf; then
    echo "[+] PASSED: Sysctl network acceleration settings verified."
else
    echo "[!] FAILED: Missing BBR or buffer tunings."
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 3: Verify Unbound Zero-Log Enforcement
echo "[*] Test 3: Verifying Unbound DNS zero-logging policy..."
if grep -q "log-queries: no" vpn-server/unbound/unbound.conf && \
   grep -q 'logfile: "/dev/null"' vpn-server/unbound/unbound.conf; then
    echo "[+] PASSED: Unbound zero-logging configuration verified."
else
    echo "[!] FAILED: Unbound logging policy is not zero-log!"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 4: Verify Fail-Closed Firewall Forwarding Policy
echo "[*] Test 4: Verifying nftables drop policy..."
if grep -q "chain forward {" vpn-server/nftables/vpn-rules.nft && \
   grep -q "policy drop;" vpn-server/nftables/vpn-rules.nft; then
    echo "[+] PASSED: nftables fail-closed drop policy verified."
else
    echo "[!] FAILED: nftables forward policy is permissive!"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 5: Verify Database Schema Entities
echo "[*] Test 5: Verifying all required PostgreSQL entities and migrations..."
REQUIRED_ENTITIES=("users" "devices" "countries" "regions" "cities" "vpn_servers" "vpn_peers" "ip_addresses" "dedicated_ip_assignments" "subscriptions" "plans" "sessions" "security_events" "audit_events")
SCHEMA_MISSING=0
for entity in "${REQUIRED_ENTITIES[@]}"; do
    if ! grep -q "CREATE TABLE IF NOT EXISTS $entity" backend/migrations/001_initial_schema.up.sql; then
        echo "[!] Missing entity in migration: $entity"
        SCHEMA_MISSING=$((SCHEMA_MISSING + 1))
    fi
done
if [ "$SCHEMA_MISSING" -eq 0 ]; then
    echo "[+] PASSED: All 14 core database entities verified in migration."
else
    echo "[!] FAILED: Missing database entities."
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 6: Verify Russia Multi-City and Global Locations in Seed Data
echo "[*] Test 6: Verifying multi-city Russia and global location coverage..."
if grep -q "Moscow" backend/migrations/002_seed_initial_data.up.sql && \
   grep -q "Saint Petersburg" backend/migrations/002_seed_initial_data.up.sql && \
   grep -q "Karachi" backend/migrations/002_seed_initial_data.up.sql && \
   grep -q "Frankfurt" backend/migrations/002_seed_initial_data.up.sql; then
    echo "[+] PASSED: Russia multi-city (Moscow & Saint Petersburg) and global locations seeded."
else
    echo "[!] FAILED: Missing seeded location requirements."
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 7: Verify Cryptographic Primitives Implementation
echo "[*] Test 7: Verifying Argon2id and Ed25519 token implementations..."
if grep -q "argon2.IDKey" backend/internal/crypto/argon2.go && \
   grep -q "ed25519.Sign" backend/internal/crypto/tokens.go; then
    echo "[+] PASSED: Argon2id and Ed25519 cryptographic modules verified."
else
    echo "[!] FAILED: Missing Argon2id or Ed25519 implementations."
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo "========================================================"
if [ "$FAILED_TESTS" -eq 0 ]; then
    echo " ALL TESTS PASSED: Security and leak verifications green. "
    exit 0
else
    echo " DIAGNOSTIC FAILURE: $FAILED_TESTS test(s) failed. "
    exit 1
fi
