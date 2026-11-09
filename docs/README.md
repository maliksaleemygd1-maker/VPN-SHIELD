# Proton Plus VPN - Documentation Index

This directory provides comprehensive documentation for the Proton Plus VPN architecture, operations, and security protocols:

1. **[ARCHITECTURE.md](../ARCHITECTURE.md)**: System topology, Data Plane vs. Control Plane separation, in-kernel WireGuard pipeline, and dedicated IP routing.
2. **[SECURITY.md](../SECURITY.md)**: Cryptographic algorithms (ChaCha20-Poly1305, Curve25519, Argon2id, TLS 1.3), keystores, fail-closed kill switch, and secret management.
3. **[PRIVACY.md](../PRIVACY.md)**: Privacy-by-design principles, zero-log policies, ephemeral in-memory state, and telemetry restrictions.
4. **[THREAT_MODEL.md](../THREAT_MODEL.md)**: Adversary models, protected attack vectors, and explicit operational limitations.
5. **[DEPLOYMENT.md](../DEPLOYMENT.md)**: Automated infrastructure provisioning, Terraform + Ansible pipelines, and Linux sysctl tuning.
6. **[API.md](../API.md)**: REST and gRPC API contract specifications for authentication, node discovery, peer registration, and leak diagnostics.
7. **[INCIDENT_RESPONSE.md](../INCIDENT_RESPONSE.md)**: Severity classifications, emergency node compromise revocation, and client leak response procedures.
8. **[DISASTER_RECOVERY.md](../DISASTER_RECOVERY.md)**: RTO/RPO targets, multi-region control plane failover, and automated node fleet disaster drills.
