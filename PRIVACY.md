# Proton Plus VPN - Privacy by Design & Zero-Log Architecture

## 1. Zero-Log Principles

Proton Plus VPN is engineered from the ground up to prevent the generation or storage of identifiable user activity data.

### Strictly Not Collected:
- **No Browsing History**: No destination IP addresses, hostnames, or URLs.
- **No DNS Query Logs**: The in-memory Unbound resolvers run with zero query logging, zero cache dumping, and strict QNAME minimisation.
- **No Traffic Metadata**: No recording of packet payloads, contents, or connection metadata.
- **No User Connection Timestamp Logs**: No timestamped log correlating user ID with real external IP addresses.

## 2. In-Memory Ephemeral Data Plane

- VPN gateway nodes run with ephemeral storage for active WireGuard interfaces.
- Peer state is maintained only inside Linux kernel memory (`wireguard.ko`).
- When a user disconnects or key lease expires, the in-kernel peer is immediately removed (`wg set wg0 peer <PUBKEY> remove`).
- Rebooting or powering down any server leaves zero disk traces of previous sessions.

## 3. Telemetry Policy

- **Operational Health Metrics**: Node-level aggregated metrics (CPU utilization %, aggregated link bandwidth Mbps, overall dropped packet rate %).
- These metrics contain zero user identifiers, zero IP addresses, and cannot be correlated to any specific subscriber.
- Advertising trackers, third-party analytics SDKs, and intrusive profiling tools are strictly forbidden in mobile clients.
