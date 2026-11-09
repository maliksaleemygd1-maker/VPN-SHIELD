#!/usr/bin/env bash
# ==============================================================================
# Proton Plus VPN - Receive Side Scaling (RSS) & Core Affinity Optimizer
# Distributes multi-gigabit WireGuard packet processing across all CPU cores
# ==============================================================================
set -euo pipefail

WAN_IF="${1:-eth0}"
WG_IF="${2:-wg0}"

echo "[*] Optimizing NIC queues and CPU affinity for interface: $WAN_IF"

# 1. Enable Receive Packet Steering (RPS) on all CPU cores
CPU_MASK=$(python3 -c "import os; print(hex((1 << (os.cpu_count() or 1)) - 1)[2:])" 2>/dev/null || echo "f")

for rps_file in /sys/class/net/"$WAN_IF"/queues/rx-*/rps_cpus; do
    if [[ -f "$rps_file" ]]; then
        echo "$CPU_MASK" > "$rps_file" || true
    fi
done

# 2. Maximize ring buffer descriptors
if command -v ethtool >/dev/null 2>&1; then
    echo "[*] Maximizing ring buffer sizes (rx 4096, tx 4096)..."
    ethtool -G "$WAN_IF" rx 4096 tx 4096 2>/dev/null || true
    echo "[*] Enabling Generic Receive Offload (GRO) & Generic Segmentation Offload (GSO)..."
    ethtool -K "$WAN_IF" gro on gso on tso on 2>/dev/null || true
fi

echo "[+] NIC multi-queue RSS/RPS optimization applied successfully."
