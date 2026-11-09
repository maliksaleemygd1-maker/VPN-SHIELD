# Proton Plus VPN - Control Plane API Specification

## 1. Base URL & Protocol

- **Base URL**: `https://api.protonplusvpn.net/v1`
- **Transport**: TLS 1.3 only
- **Authentication**: Bearer token via PASETO v4.public or Ed25519 JWT header: `Authorization: Bearer <TOKEN>`

---

## 2. Authentication Endpoints

### `POST /auth/register`
Creates user account with Argon2id password derivation.
```json
{
  "email": "user@example.com",
  "password": "StrongPassword123#!"
}
```
**Response 201 Created**:
```json
{
  "user_id": "usr_9a4f21",
  "access_token": "v4.public....",
  "refresh_token": "v4.public....",
  "expires_in": 900
}
```

### `POST /auth/login`
Authenticates user and returns fresh session tokens.

---

## 3. Server Discovery & Dynamic Topology

### `GET /servers`
Retrieves live database-driven global server list with real-time health, latency benchmarks, and capacities.
**Response 200 OK**:
```json
{
  "updated_at": 1773665200,
  "servers": [
    {
      "id": "srv_de_fra_01",
      "country_code": "DE",
      "country_name": "Germany",
      "city_name": "Frankfurt",
      "endpoint_ip": "185.120.44.12",
      "port": 51820,
      "public_key": "x5G0zWbF7Yw0vO4t91RkQmB...=",
      "current_load_percent": 24,
      "avg_latency_ms": 14,
      "packet_loss_percent": 0.0,
      "capacity_mbps": 10000,
      "supports_dedicated_ip": true,
      "is_active": true
    },
    {
      "id": "srv_ru_mow_01",
      "country_code": "RU",
      "country_name": "Russia",
      "city_name": "Moscow",
      "endpoint_ip": "95.173.136.50",
      "port": 51820,
      "public_key": "pL8bW...=",
      "current_load_percent": 31,
      "avg_latency_ms": 32,
      "packet_loss_percent": 0.1,
      "capacity_mbps": 10000,
      "supports_dedicated_ip": true,
      "is_active": true
    },
    {
      "id": "srv_pk_khi_01",
      "country_code": "PK",
      "country_name": "Pakistan",
      "city_name": "Karachi",
      "endpoint_ip": "111.119.160.20",
      "port": 51820,
      "public_key": "qK1mX...=",
      "current_load_percent": 42,
      "avg_latency_ms": 48,
      "packet_loss_percent": 0.2,
      "capacity_mbps": 5000,
      "supports_dedicated_ip": false,
      "is_active": true
    }
  ]
}
```

---

## 4. Peer WireGuard Provisioning

### `POST /vpn/peer/register`
Zero-knowledge peer registration. Client submits only their device Curve25519 public key.
```json
{
  "server_id": "srv_de_fra_01",
  "client_public_key": "W3v9+qLp2/eYf0+1Xz...=",
  "device_name": "Pixel 9 Pro",
  "platform": "android"
}
```
**Response 200 OK**:
```json
{
  "assigned_internal_ipv4": "10.64.12.84/32",
  "assigned_internal_ipv6": "fd00:64::1284/128",
  "server_public_key": "x5G0zWbF7Yw0vO4t91RkQmB...=",
  "server_endpoint": "185.120.44.12:51820",
  "dns_servers": ["10.64.0.1", "fd00::1"],
  "allowed_ips": ["0.0.0.0/0", "::/0"],
  "mtu": 1420
}
```

---

## 5. Dedicated IP Allocation

### `POST /dedicated-ip/allocate`
Allocates a fixed dedicated public IP to the authenticated customer.
```json
{
  "region": "US-EAST",
  "country_code": "US",
  "city_name": "Ashburn"
}
```
**Response 200 OK**:
```json
{
  "dedicated_ip_id": "dip_7721",
  "public_ip": "199.180.12.44",
  "server_id": "srv_us_ash_ded_01",
  "status": "active",
  "assigned_at": 1773665200
}
```

---

## 6. Diagnostic & Leak Testing

### `GET /diagnostics/leak-test`
Echoes back the egress IP, detected DNS resolver, and WebRTC status seen by the API.
```json
{
  "egress_ip": "185.120.44.12",
  "is_vpn_ip": true,
  "vpn_server_id": "srv_de_fra_01",
  "detected_dns_resolver": "10.64.0.1",
  "dns_leak_detected": false,
  "ipv6_leak_detected": false
}
```
