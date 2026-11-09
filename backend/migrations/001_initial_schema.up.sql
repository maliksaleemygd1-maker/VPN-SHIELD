-- Migration 001: Initial Schema for Proton Plus VPN
-- PostgreSQL 16 compatible schema with strict relational integrity and indexing

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Plans & Subscriptions
CREATE TABLE IF NOT EXISTS plans (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    max_devices INT NOT NULL DEFAULT 5,
    allows_dedicated_ip BOOLEAN NOT NULL DEFAULT FALSE,
    max_bandwidth_mbps INT NOT NULL DEFAULT 1000,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'user', -- 'user', 'admin', 'netops', 'support'
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id VARCHAR(50) NOT NULL REFERENCES plans(id),
    status VARCHAR(30) NOT NULL DEFAULT 'active', -- 'active', 'expired', 'canceled'
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Devices
CREATE TABLE IF NOT EXISTS devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name VARCHAR(100) NOT NULL,
    platform VARCHAR(30) NOT NULL, -- 'android', 'ios', 'extension', 'linux', 'macos', 'windows'
    wireguard_public_key VARCHAR(64) UNIQUE NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    last_seen_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Geographic Hierarchy
CREATE TABLE IF NOT EXISTS countries (
    code VARCHAR(2) PRIMARY KEY, -- ISO 3166-1 alpha-2
    name VARCHAR(100) NOT NULL,
    flag_emoji VARCHAR(10) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS regions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_code VARCHAR(2) NOT NULL REFERENCES countries(code) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_code VARCHAR(2) NOT NULL REFERENCES countries(code) ON DELETE CASCADE,
    region_id UUID REFERENCES regions(id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- VPN Servers & Nodes
CREATE TABLE IF NOT EXISTS vpn_servers (
    id VARCHAR(64) PRIMARY KEY,
    city_id UUID NOT NULL REFERENCES cities(id) ON DELETE CASCADE,
    hostname VARCHAR(255) NOT NULL,
    public_ipv4 INET NOT NULL,
    public_ipv6 INET,
    wireguard_port INT NOT NULL DEFAULT 51820,
    server_public_key VARCHAR(64) NOT NULL,
    capacity_mbps INT NOT NULL DEFAULT 10000,
    current_load_percent INT NOT NULL DEFAULT 0,
    avg_latency_ms INT NOT NULL DEFAULT 20,
    packet_loss_percent NUMERIC(5, 2) NOT NULL DEFAULT 0.00,
    historical_reliability_score NUMERIC(5, 2) NOT NULL DEFAULT 99.90,
    supports_dedicated_ip BOOLEAN NOT NULL DEFAULT FALSE,
    drain_mode BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_heartbeat_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Active VPN Peers (In-Memory Synchronization)
CREATE TABLE IF NOT EXISTS vpn_peers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    server_id VARCHAR(64) NOT NULL REFERENCES vpn_servers(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    client_public_key VARCHAR(64) NOT NULL,
    assigned_ipv4 INET NOT NULL,
    assigned_ipv6 INET,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    connected_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_handshake_at TIMESTAMPTZ,
    UNIQUE (server_id, assigned_ipv4),
    UNIQUE (server_id, client_public_key)
);

-- IP Pool Management & Dedicated IPs
CREATE TABLE IF NOT EXISTS ip_addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    server_id VARCHAR(64) NOT NULL REFERENCES vpn_servers(id) ON DELETE CASCADE,
    ip_address INET UNIQUE NOT NULL,
    ip_type VARCHAR(20) NOT NULL DEFAULT 'shared', -- 'shared', 'dedicated'
    is_allocated BOOLEAN NOT NULL DEFAULT FALSE,
    allocated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS dedicated_ip_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id UUID REFERENCES devices(id) ON DELETE SET NULL,
    server_id VARCHAR(64) NOT NULL REFERENCES vpn_servers(id) ON DELETE CASCADE,
    ip_address INET UNIQUE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'active', -- 'active', 'quarantine', 'released'
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- User Sessions & Audit Logs
CREATE TABLE IF NOT EXISTS sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id UUID REFERENCES devices(id) ON DELETE SET NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255),
    client_ip INET,
    expires_at TIMESTAMPTZ NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS security_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'info', -- 'info', 'warning', 'critical'
    description TEXT NOT NULL,
    ip_address INET,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(50) NOT NULL,
    target_id VARCHAR(64) NOT NULL,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indices for Low-Latency Query Execution
CREATE INDEX IF NOT EXISTS idx_vpn_servers_active ON vpn_servers(is_active, drain_mode, current_load_percent);
CREATE INDEX IF NOT EXISTS idx_vpn_servers_city ON vpn_servers(city_id);
CREATE INDEX IF NOT EXISTS idx_cities_country ON cities(country_code);
CREATE INDEX IF NOT EXISTS idx_devices_user ON devices(user_id);
CREATE INDEX IF NOT EXISTS idx_devices_pubkey ON devices(wireguard_public_key);
CREATE INDEX IF NOT EXISTS idx_vpn_peers_server ON vpn_peers(server_id, is_active);
CREATE INDEX IF NOT EXISTS idx_dedicated_ip_user ON dedicated_ip_assignments(user_id, status);
CREATE INDEX IF NOT EXISTS idx_sessions_user ON sessions(user_id, is_revoked);
