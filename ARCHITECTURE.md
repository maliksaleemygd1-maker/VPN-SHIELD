# Proton Plus VPN - Comprehensive Architecture

## 1. System Topology Overview

Proton Plus VPN enforces a complete architectural decoupling of the **Data Plane** from the **Control Plane**:

```
+---------------------------------------------------------------------------------+
|                                 CLIENT CLIENTS                                  |
|   Android (VpnService)      iOS (NEPacketTunnel)     Browser Ext (Manifest V3)  |
+---------------------------------------------------------------------------------+
                                      |
                         TLS 1.3 / PASETO Bearer Auth
                                      v
+---------------------------------------------------------------------------------+
|                              CONTROL PLANE LAYER                                |
|   Envoy API Gateway -> Go Backend Engine                                        |
|   ├── /auth             : Argon2id + Ed25519 token signing                      |
|   ├── /nodes            : Real-time latency, load & capacity topology matrix    |
|   ├── /peers            : Zero-knowledge Curve25519 public-key registration     |
|   └── /dedicated-ip     : Hardware NAT CIDR leasing engine                      |
+---------------------------------------------------------------------------------+
                                      |
                      Internal WireGuard Mesh / mTLS gRPC
                                      v
+---------------------------------------------------------------------------------+
|                             DATA PLANE EDGE NODES                               |
|   Linux 6.x Kernel with In-Kernel wireguard.ko                                  |
|   ├── Multi-Queue NIC   : RSS/RPS core affinity                                 |
|   ├── Network Tunings   : BBR, 64MB buffers, MTU 1420 MSS Clamping              |
|   ├── nftables Fastpath : Stateless ingress filtering + SNAT/DNAT               |
|   └── Local DNS Cache   : In-memory Unbound recursion (QNAME minimisation)      |
+---------------------------------------------------------------------------------+
```

## 2. Data Plane vs Control Plane Separation

- **Data Plane (VPN Node)**:
  - Responsible solely for routing encrypted UDP packets between clients and the internet.
  - No database connectivity or user identity state.
  - Ephemeral peer table kept strictly in kernel memory.
  - Metrics collected by the local Rust node daemon are aggregated counters (total active peers, total egress bandwidth) pushed upstream via mTLS.
- **Control Plane**:
  - Handles authentication, subscription entitlements, device registration, and dynamic node health scoring.
  - Clients never expose their private WireGuard key to the control plane; only client public keys are registered.

## 3. High-Speed WireGuard Performance Pipeline

1. **In-Kernel Processing**: Eliminates costly user-space/kernel context switches by using the Linux kernel module directly.
2. **Buffer Allocation**: Linux receive and write buffers set to 64MB each to absorb high-bandwidth bursts without packet drops.
3. **Congestion Management**: Google BBR ensures maximum throughput over long-haul high-latency cross-border links.
4. **MTU Optimization**: Clamped to 1420 bytes for standard 1500-byte WAN connections, accounting for:
   - IPv4 header: 20 bytes
   - UDP header: 8 bytes
   - WireGuard header: 32 bytes
   - Authentication tag: 16 bytes
   - Overhead margin: 4 bytes -> total 80 bytes overhead (1500 - 80 = 1420).

## 4. Dedicated IP Architecture

- Dedicated IP addresses are mapped to dedicated egress interfaces on selected high-capacity clusters.
- When a user connects with a dedicated IP entitlement:
  - A unique private tunnel IP (`10.128.x.x`) is allocated.
  - Node `nftables` executes:
    `ip saddr 10.128.x.x snat to <DEDICATED_PUBLIC_IPV4>`
  - The public IP remains immutable and dedicated solely to that account for the duration of the reservation.
