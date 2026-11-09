#!/usr/bin/env bash
# ==============================================================================
# Proton Plus VPN - Dedicated 1:1 Static IP NAT Provisioner
# Configures bidirectional stateless 1:1 NAT and strict isolation rules
# ==============================================================================
set -euo pipefail

SUBSCRIBER_VIRTUAL_IP="${1:-10.64.10.2}"
DEDICATED_PUBLIC_IP="${2:-199.180.20.101}"
WAN_IF="${3:-eth0}"
WG_IF="${4:-wg0}"

echo "[*] Provisioning 1:1 Static Dedicated IP Mapping: $SUBSCRIBER_VIRTUAL_IP <---> $DEDICATED_PUBLIC_IP"

# 1. Ensure IP alias is assigned to the WAN interface
if ! ip addr show dev "$WAN_IF" | grep -q "$DEDICATED_PUBLIC_IP"; then
    echo "[*] Adding IP alias $DEDICATED_PUBLIC_IP to $WAN_IF..."
    ip addr add "$DEDICATED_PUBLIC_IP/32" dev "$WAN_IF"
fi

# 2. Add 1:1 SNAT (Egress) - outgoing traffic uses dedicated IP
nft add rule ip nat postrouting ip saddr "$SUBSCRIBER_VIRTUAL_IP" oifname "$WAN_IF" snat to "$DEDICATED_PUBLIC_IP"

# 3. Add 1:1 DNAT (Ingress) - inbound responses route to dedicated subscriber
nft add rule ip nat prerouting iifname "$WAN_IF" ip daddr "$DEDICATED_PUBLIC_IP" dnat to "$SUBSCRIBER_VIRTUAL_IP"

# 4. Strict Isolation: prevent dedicated IP client from probing other VPN peers
nft add rule inet filter forward iifname "$WG_IF" oifname "$WG_IF" ip saddr "$SUBSCRIBER_VIRTUAL_IP" drop

echo "[+] Dedicated IP $DEDICATED_PUBLIC_IP successfully bound to subscriber $SUBSCRIBER_VIRTUAL_IP."
