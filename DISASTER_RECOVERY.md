# Proton Plus VPN - Disaster Recovery & Business Continuity

## 1. Recovery Time Objective (RTO) & Recovery Point Objective (RPO)

- **Control Plane API**: RTO < 5 minutes (multi-region active-passive failover), RPO < 60 seconds (replicated PostgreSQL WAL).
- **Edge VPN Fleet**: RTO < 60 seconds (client auto-reconnects to alternative nearby node in < 1.5s upon node unreachability).
- **Dedicated IPs**: RTO < 15 minutes (automatic IP migration to standby node within identical datacenter subnet).

## 2. Multi-Region Control Plane Architecture

- Primary Control Plane in EU (Frankfurt) with hot standby in US (Ashburn).
- Continuous streaming replication of user credentials and subscription entitlements.
- Edge nodes maintain local cached peer validations for 2 hours in case of transient total control plane disconnection, ensuring connected users experience zero traffic interruption.

## 3. Node Fleet Disaster Drills

- Bi-weekly simulated abrupt server termination drills to verify client auto-reconnect and fail-closed Kill Switch resilience under unexpected link cuts.
