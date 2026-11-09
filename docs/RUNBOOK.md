# Proton Plus VPN - Production NetOps Runbook & Incident Response

## 1. Zero-Log Architecture Compliance Verification
To independently verify zero user browsing or DNS data is recorded:
1. **Unbound DNS Query Verification**:
   ```bash
   # Confirm query logs are directed to /dev/null and verbosity is 0
   grep -E 'verbosity|log-queries|logfile' /etc/unbound/unbound.conf
   # Ensure zero file descriptors exist on disk for dns queries
   lsof -c unbound | grep -i log
   ```
2. **WireGuard Ephemeral State**:
   - WireGuard handshakes are held in Linux RAM only.
   - Upon reboot, container tear-down, or service restart, dynamic peer telemetry vanishes immediately.

## 2. Emergency Edge Node Drain
If an edge PoP experiences upstream transit issues, BGP route flapping, or physical infrastructure compromise:
```bash
# 1. Update node status to drain in control-plane API
curl -X POST https://api.protonplusvpn.net/v1/admin/nodes/srv_de_fra_01/drain \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 2. Instruct node daemon to smoothly hand off active subscribers
python3 /vpn-server/daemon/node_daemon.py --drain-active-peers
```

## 3. Dedicated IP Hardware NAT Reassignment
When a subscriber releases or modifies their dedicated IP:
```bash
# Run quarantine manager to enforce 24-hour cooling off period
python3 /vpn-server/bin/dedicated-ip-manager.py

# Unbind stateless nftables rules
nft delete rule ip nat postrouting handle <RULE_HANDLE>
```

## 4. Key Compromise Procedure
1. Trigger `KeyRotationManager` to generate fresh Curve25519 public/private keys.
2. Broadcast new public key to mobile clients via Envoy API gateway.
3. Reload WireGuard interface with `wg syncconf wg0 <(wg-quick strip wg0)`.
