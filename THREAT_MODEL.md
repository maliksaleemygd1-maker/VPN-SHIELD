# Proton Plus VPN - Threat Model & Operational Boundaries

## 1. Scope & Adversary Capabilities

This document assesses the defenses of Proton Plus VPN against realistic threat actors, ranging from untrusted local network operators to sophisticated internet traffic adversaries.

## 2. Protected Scenarios & Defenses

| Threat Scenario | Threat Actor | Proton Plus Mitigation |
| :--- | :--- | :--- |
| **Public Wi-Fi Sniffing** | Rogue access point, local eavesdropper | ChaCha20-Poly1305 full payload and header encryption. MITM impossible. |
| **ISP Snooping & Throttling** | Local ISP / Telecommunications Carrier | All traffic multiplexed inside opaque WireGuard UDP packets to VPN IP. ISP sees only encrypted datagrams. |
| **DNS Hijacking & Poisoning** | Malicious local DNS / ISP cache poisoning | All DNS queries routed through encrypted tunnel to private internal Unbound server (`10.64.0.1`) with DNSSEC validation. |
| **IPv6 Leakage** | Dual-stack ISP pushing IPv6 bypass | In-kernel IPv6 route encapsulation (`::/0`) or system-level blackhole rule preventing packet escape. |
| **Replay Attacks** | Network interceptor | WireGuard monotonic 64-bit counter and BLAKE2s authenticated anti-replay cookie mechanisms. |
| **Server Seizure / Confiscation** | Hostile physical datacenter intrusion | RAM-only peer tables, encrypted root partition, no user connection logs on disk. Private server keys rotated dynamically. |

## 3. Explicit System Limitations (No Impossible Claims)

Proton Plus VPN does **NOT** promise absolute anonymity or immunity against all vectors:

1. **Compromised Endpoints**: If malware, keyloggers, or pegasus-style spyware are running on the user's mobile device with OS-level privileges, network-level encryption cannot prevent screenshotting, credential harvesting, or memory dumping.
2. **Browser Fingerprinting & Identifiers**: Websites can track users across sessions using Canvas fingerprinting, TLS client hello fingerprinting, WebGL hashes, and logged-in account cookies regardless of VPN IP address.
3. **Hardware Sensors & Geolocation**: Mobile applications with Android Location / GPS permissions or cell tower triangulation bypass IP geolocation.
4. **Global Passive Traffic Correlation**: A nation-state adversary observing both ingress traffic into the VPN node and egress traffic from the node to a target website simultaneously can use statistical timing analysis to correlate connections.
