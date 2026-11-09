# Proton Plus VPN (VeloShield Core)

Production-grade global mobile VPN platform engineered for maximum real-world throughput, ultra-low latency, defense-in-depth security, and zero-log privacy.

## Platform Priorities

1. **Maximum Practical Internet Speed**: Native in-kernel WireGuard (`wireguard.ko`), Google BBR congestion control, multi-queue RSS/RPS NIC optimizations, MTU clamping (1420 bytes), and zero packet fragmentation.
2. **Strong Practical Security**: ChaCha20-Poly1305 encryption, Curve25519 key exchange, BLAKE2s hashing, client-side key generation (hardware keystore), TLS 1.3 control plane, and Argon2id authentication.
3. **Sub-Millisecond Routing & Ultra-Low Latency**: In-memory recursive DNS with Unbound, direct BGP routing, and real-time multi-metric server selection (latency, loss, load, capacity).
4. **Reliable Seamless Roaming**: Native Android `VpnService` and iOS `NEPacketTunnelProvider` with automatic Wi-Fi <-> Cellular endpoint migration.
5. **Global Coverage**: 50+ countries including multi-city presence in Russia (Moscow, Saint Petersburg), Pakistan (Karachi, Islamabad), Europe, Americas, Asia, and MEA.
6. **Dedicated / Static IP Support**: Guaranteed 1:1 public IP binding with hardware NAT isolation.
7. **Zero-Log Privacy Architecture**: RAM-only operational telemetry, zero activity logging, no browsing or DNS history recording.

---

## Repository Structure

```
├── android/                 # Android Native Client (Kotlin, Jetpack Compose, VpnService)
│   └── app/                 # App module with Room DB, MVVM, and Material 3 UI
├── ios/                     # iOS Native Client (Swift, NetworkExtension, WireGuardKit)
├── backend/                 # Control Plane API (Go 1.22+, PostgreSQL, Redis)
├── control-plane/           # API Gateway (Envoy), gRPC services, Token & Peer coordination
├── vpn-server/              # Data Plane node automation (Sysctl tuning, nftables, Unbound DNS)
├── browser-extension/       # Companion Browser Extensions (Chromium & Firefox Manifest V3)
├── admin-dashboard/         # Observability & Node Fleet Administration Dashboard
├── shared/                  # Shared Protobuf schemas, API models, and protocol contracts
├── terraform/               # Multi-cloud IaC provisioning (AWS, Hetzner, Equinix, Bare-metal)
├── ansible/                 # Node hardening, sysctl tuning, and automated deployment playbooks
├── tests/                   # Automated security, DNS/IPv6 leak, and performance test suites
└── docs/                    # Architecture, Security, Privacy, Threat Model, and Runbooks
```

## Quick Start & Verification

- **Android App**: Run `./gradlew assembleDebug` or build in AI Studio.
- **Backend & Control Plane**: Review `/backend` and `/control-plane` specifications.
- **Security & Leak Verification**: Execute `./tests/security_leak_test.sh`.
