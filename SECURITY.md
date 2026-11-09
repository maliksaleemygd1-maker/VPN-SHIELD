# Proton Plus VPN - Security Architecture & Standards

## 1. Cryptographic Primitives & Implementation

Proton Plus VPN strictly employs modern, audited cryptographic standards. Custom cryptography is forbidden.

- **Symmetric Encryption**: ChaCha20 with Poly1305 authenticator (RFC 8439) for all data packets inside the WireGuard tunnel.
- **Key Exchange**: Curve25519 (RFC 7748) for high-speed Diffie-Hellman ephemeral key agreements.
- **Hashing & HKDF**: BLAKE2s for hashing and HMAC construction; Argon2id for password derivation.
- **Transport Security (Control Plane)**: TLS 1.3 enforced with forward secrecy ciphers (`TLS_AES_256_GCM_SHA384`, `TLS_CHACHA20_POLY1305_SHA256`). Minimum TLS version 1.3.

## 2. Key Management & Hardware Keystores

- **Client-Side Key Generation**: Curve25519 private keys are generated directly inside the Android Keystore (backed by TEE / StrongBox) or iOS Secure Enclave.
- **Zero-Knowledge Backend**: The server control plane registers only the client public key. Private keys never leave the user's mobile device under any circumstance.
- **Perfect Forward Secrecy (PFS)**: WireGuard automatically rotates symmetric session encryption keys every 180 seconds or every 5 GiB of data transfer.

## 3. Kill Switch & Leak Protection Architecture

- **Fail-Closed Default**:
  - Android: Bound to `android.net.VpnService` with `disallowBypass()` and strict packet socket filtering.
  - iOS: `NETunnelProviderProtocol` with `includeAllNetworks = true`.
- **DNS Leak Protection**:
  - All DNS queries forced to internal loopback Unbound resolvers (`10.64.0.1` / `fd00::1`).
  - Strict blocking of outbound port 53 / 853 UDP and TCP traffic to external IPs.
- **IPv6 Leak Protection**:
  - Full dual-stack tunnel routing (`::/0`), or deterministic blackholing of IPv6 at the OS network interface to eliminate side-channel bypass.
- **WebRTC Leak Mitigation**:
  - In browser extensions: enforcement of `disable_non_proxied_udp`.

## 4. Hardening, RBAC & Secret Management

- **Hardened Linux Nodes**: Kernel module signing, AppArmor profiles, read-only root filesystems where possible, disabled SSH password authentication (ED25519 keys only via bastion).
- **Role-Based Access Control**:
  - `Admin`: Cluster orchestration and node admission.
  - `NetOps`: Telemetry, drain mode, capacity allocation.
  - `Support`: Account entitlement verification without access to keys or traffic.
- **Automated Secret Scanning**: Pre-commit hooks, CI gitleaks scanning, and ephemeral dynamic secrets managed by HashiCorp Vault.
