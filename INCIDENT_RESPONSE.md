# Proton Plus VPN - Incident Response Playbook

## 1. Incident Severity Classification

- **SEV-1 (Critical)**: Control plane outage, potential key exposure, confirmed traffic leakage on released client version, or infrastructure compromise.
- **SEV-2 (High)**: Major regional node cluster offline (>25% fleet degraded), elevated packet loss in core hubs.
- **SEV-3 (Medium)**: Single edge node failure (auto-evacuated by dynamic selector).
- **SEV-4 (Low)**: Minor diagnostic telemetry reporting anomaly.

## 2. Emergency Node Compromise Protocol

If an edge VPN server is suspected of compromise:
1. **Immediate Revocation**: Mark node state `REVOKED` in Control Plane database.
2. **Drain Peers**: Control Plane pushes command to node agent to flush all active WireGuard peers and terminate `wg0`.
3. **Blackhole BGP**: Withdraw anycast BGP routing announcement from Tier-1 upstreams.
4. **Forensic Snapshot & Destroy**: Snapshot memory for isolated analysis, revoke node mTLS certificates, wipe server disk remotely, rebuild via Terraform.

## 3. Client Leak Response

If a leak vector is identified in an OS update:
1. Trigger server-side kill signal to client app via push notification.
2. Enforce fail-closed mode preventing connection until patched client is installed.
3. Publish transparent vulnerability disclosure and post-mortem within 72 hours.
