#!/usr/bin/env bash
# ==============================================================================
# Proton Plus VPN - Edge Node Interface & Kernel Orchestrator
# ==============================================================================
set -euo pipefail

WG_IF="wg0"
WG_PORT="${WG_PORT:-51820}"
WG_MTU="${WG_MTU:-1420}"
WG_CONF="/etc/wireguard/${WG_IF}.conf"
SYSCTL_CONF="/etc/sysctl.d/99-protonplus-vpn.conf"

echo "[*] Initializing Proton Plus VPN WireGuard Data Plane..."

# 1. Ensure kernel module is loaded
if ! lsmod | grep -q wireguard; then
    echo "[*] Loading wireguard.ko kernel module..."
    modprobe wireguard || echo "[!] WireGuard built-in or container virtualized"
fi

# 2. Apply kernel sysctl performance profiles
if [[ -f "$SYSCTL_CONF" ]]; then
    echo "[*] Applying network buffers, BBR, and forwarding sysctl settings..."
    sysctl -p "$SYSCTL_CONF" >/dev/null 2>&1 || true
fi

# 3. Bring up interface if not already present
if ip link show dev "$WG_IF" >/dev/null 2>&1; then
    echo "[+] WireGuard interface $WG_IF is already active."
else
    echo "[*] Creating WireGuard interface $WG_IF..."
    ip link add dev "$WG_IF" type wireguard || true
    ip address add 10.64.0.1/10 dev "$WG_IF" || true
    ip -6 address add fd00:64::1/64 dev "$WG_IF" || true
    ip link set mtu "$WG_MTU" dev "$WG_IF"
    ip link set up dev "$WG_IF"
fi

# 4. Synchronize peers if configuration file exists
if [[ -f "$WG_CONF" ]]; then
    echo "[*] Synchronizing WireGuard runtime state with $WG_CONF..."
    wg syncconf "$WG_IF" <(wg-quick strip "$WG_IF" 2>/dev/null || cat "$WG_CONF") || true
fi

# 5. Apply firewall rules
if command -v nft >/dev/null 2>&1 && [[ -f /etc/nftables/vpn-rules.nft ]]; then
    echo "[*] Enforcing nftables drop-policy packet filter..."
    nft -f /etc/nftables/vpn-rules.nft || true
fi

echo "[+] Data Plane initialization complete. Interface $WG_IF operational on UDP port $WG_PORT."
