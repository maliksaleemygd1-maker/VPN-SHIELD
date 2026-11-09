# Proton Plus VPN - Deployment & Infrastructure Guide

## 1. Prerequisites

- **Control Plane**: Kubernetes 1.28+ or Docker Swarm, PostgreSQL 16 with TimescaleDB, Redis 7 Cluster.
- **Data Plane (VPN Nodes)**: Ubuntu 24.04 LTS bare metal or KVM instance with modern kernel (6.8+), minimum 1 Gbps (recommended 10 Gbps) network interface.

## 2. Server Provisioning Pipeline

Automated deployment executes via Terraform + Ansible:

```
[Terraform Provider]
   -> Provisions VM/Bare Metal in Target Country (e.g. Russia, Pakistan, Germany, US)
   -> Attaches public IPv4 & IPv6 addresses
       |
[Ansible Execution]
   ├── 01_os_hardening.yml     : Configures sysctl, disables password SSH, sets UFW/nftables
   ├── 02_kernel_tuning.yml    : Applies BBR, 64MB buffers, RPS/RSS multiqueue
   ├── 03_wireguard_setup.yml  : Generates server keypair, initializes wg0 interface
   ├── 04_unbound_dns.yml      : Starts local recursive DNSSEC resolver on 10.64.0.1
   └── 05_node_agent.yml       : Starts Rust node daemon connecting to Control Plane
       |
[Control Plane Health Probe]
   -> Executes end-to-end handshake & leak test
   -> Marks node STATUS_ACTIVE in global directory
```

## 3. Kernel Configuration Verification

Verify sysctl parameters on the VPN node:
```bash
sysctl net.ipv4.ip_forward net.ipv4.tcp_congestion_control net.core.default_qdisc
```
Expected output:
```
net.ipv4.ip_forward = 1
net.ipv4.tcp_congestion_control = bbr
net.core.default_qdisc = fq
```
