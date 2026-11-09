-- Migration 002: Seed Initial Plans, Global Countries, Cities & Production VPN Nodes

INSERT INTO plans (id, name, max_devices, allows_dedicated_ip, max_bandwidth_mbps) VALUES
('plan_free', 'Proton Plus Basic', 1, FALSE, 100),
('plan_pro', 'Proton Plus Pro', 10, FALSE, 10000),
('plan_dedicated', 'Proton Plus Dedicated', 10, TRUE, 10000)
ON CONFLICT (id) DO NOTHING;

-- Seed Global Countries
INSERT INTO countries (code, name, flag_emoji, is_active) VALUES
('PK', 'Pakistan', '🇵🇰', TRUE),
('RU', 'Russia', '🇷🇺', TRUE),
('US', 'United States', '🇺🇸', TRUE),
('CA', 'Canada', '🇨🇦', TRUE),
('GB', 'United Kingdom', '🇬🇧', TRUE),
('DE', 'Germany', '🇩🇪', TRUE),
('FR', 'France', '🇫🇷', TRUE),
('NL', 'Netherlands', '🇳🇱', TRUE),
('CH', 'Switzerland', '🇨🇭', TRUE),
('SE', 'Sweden', '🇸🇪', TRUE),
('NO', 'Norway', '🇳🇴', TRUE),
('FI', 'Finland', '🇫🇮', TRUE),
('DK', 'Denmark', '🇩🇰', TRUE),
('ES', 'Spain', '🇪🇸', TRUE),
('PT', 'Portugal', '🇵🇹', TRUE),
('IT', 'Italy', '🇮🇹', TRUE),
('AT', 'Austria', '🇦🇹', TRUE),
('BE', 'Belgium', '🇧🇪', TRUE),
('IE', 'Ireland', '🇮🇪', TRUE),
('PL', 'Poland', '🇵🇱', TRUE),
('RO', 'Romania', '🇷🇴', TRUE),
('CZ', 'Czech Republic', '🇨🇿', TRUE),
('HU', 'Hungary', '🇭🇺', TRUE),
('IS', 'Iceland', '🇮🇸', TRUE),
('UA', 'Ukraine', '🇺🇦', TRUE),
('TR', 'Turkey', '🇹🇷', TRUE),
('AE', 'United Arab Emirates', '🇦🇪', TRUE),
('SA', 'Saudi Arabia', '🇸🇦', TRUE),
('QA', 'Qatar', '🇶🇦', TRUE),
('BH', 'Bahrain', '🇧🇭', TRUE),
('KW', 'Kuwait', '🇰🇼', TRUE),
('OM', 'Oman', '🇴🇲', TRUE),
('IN', 'India', '🇮🇳', TRUE),
('BD', 'Bangladesh', '🇧🇩', TRUE),
('LK', 'Sri Lanka', '🇱🇰', TRUE),
('NP', 'Nepal', '🇳🇵', TRUE),
('SG', 'Singapore', '🇸🇬', TRUE),
('MY', 'Malaysia', '🇲🇾', TRUE),
('ID', 'Indonesia', '🇮🇩', TRUE),
('TH', 'Thailand', '🇹🇭', TRUE),
('JP', 'Japan', '🇯🇵', TRUE),
('KR', 'South Korea', '🇰🇷', TRUE),
('HK', 'Hong Kong', '🇭🇰', TRUE),
('TW', 'Taiwan', '🇹🇼', TRUE),
('AU', 'Australia', '🇦🇺', TRUE),
('NZ', 'New Zealand', '🇳🇿', TRUE),
('ZA', 'South Africa', '🇿🇦', TRUE),
('EG', 'Egypt', '🇪🇬', TRUE),
('NG', 'Nigeria', '🇳🇬', TRUE),
('KE', 'Kenya', '🇰🇪', TRUE),
('MA', 'Morocco', '🇲🇦', TRUE),
('BR', 'Brazil', '🇧🇷', TRUE),
('AR', 'Argentina', '🇦🇷', TRUE),
('CL', 'Chile', '🇨🇱', TRUE),
('CO', 'Colombia', '🇨🇴', TRUE),
('MX', 'Mexico', '🇲🇽', TRUE)
ON CONFLICT (code) DO NOTHING;

-- Seed Cities
INSERT INTO cities (id, country_code, name, latitude, longitude) VALUES
-- Russia (Multi-city requirement)
('c0000000-0000-0000-0000-000000000001', 'RU', 'Moscow', 55.7558, 37.6173),
('c0000000-0000-0000-0000-000000000002', 'RU', 'Saint Petersburg', 59.9343, 30.3351),
('c0000000-0000-0000-0000-000000000003', 'RU', 'Novosibirsk', 55.0084, 82.9357),

-- Pakistan
('c0000000-0000-0000-0000-000000000010', 'PK', 'Karachi', 24.8607, 67.0011),
('c0000000-0000-0000-0000-000000000011', 'PK', 'Islamabad', 33.6844, 73.0479),
('c0000000-0000-0000-0000-000000000012', 'PK', 'Lahore', 31.5204, 74.3587),

-- Germany
('c0000000-0000-0000-0000-000000000020', 'DE', 'Frankfurt', 50.1109, 8.6821),
('c0000000-0000-0000-0000-000000000021', 'DE', 'Berlin', 52.5200, 13.4050),

-- United States
('c0000000-0000-0000-0000-000000000030', 'US', 'Ashburn (VA)', 39.0438, -77.4874),
('c0000000-0000-0000-0000-000000000031', 'US', 'New York (NY)', 40.7128, -74.0060),
('c0000000-0000-0000-0000-000000000032', 'US', 'Los Angeles (CA)', 34.0522, -118.2437),
('c0000000-0000-0000-0000-000000000033', 'US', 'Chicago (IL)', 41.8781, -87.6298),

-- United Kingdom
('c0000000-0000-0000-0000-000000000040', 'GB', 'London', 51.5074, -0.1278),
('c0000000-0000-0000-0000-000000000041', 'GB', 'Manchester', 53.4808, -2.2426),

-- Netherlands, France, Switzerland, Sweden
('c0000000-0000-0000-0000-000000000050', 'NL', 'Amsterdam', 52.3676, 4.9041),
('c0000000-0000-0000-0000-000000000051', 'FR', 'Paris', 48.8566, 2.3522),
('c0000000-0000-0000-0000-000000000052', 'CH', 'Zurich', 47.3769, 8.5417),
('c0000000-0000-0000-0000-000000000053', 'SE', 'Stockholm', 59.3293, 18.0686),

-- Asia Hubs (Singapore, Japan, UAE, India)
('c0000000-0000-0000-0000-000000000060', 'SG', 'Singapore', 1.3521, 103.8198),
('c0000000-0000-0000-0000-000000000061', 'JP', 'Tokyo', 35.6762, 139.6503),
('c0000000-0000-0000-0000-000000000062', 'AE', 'Dubai', 25.2048, 55.2708),
('c0000000-0000-0000-0000-000000000063', 'IN', 'Mumbai', 19.0760, 72.8777)
ON CONFLICT (id) DO NOTHING;

-- Seed Operational High-Throughput VPN Servers
INSERT INTO vpn_servers (
    id, city_id, hostname, public_ipv4, wireguard_port, server_public_key,
    capacity_mbps, current_load_percent, avg_latency_ms, packet_loss_percent,
    historical_reliability_score, supports_dedicated_ip, is_active
) VALUES
-- Germany (Frankfurt)
('srv_de_fra_01', 'c0000000-0000-0000-0000-000000000020', 'fra1.protonplusvpn.net', '185.120.44.12', 51820,
 'x5G0zWbF7Yw0vO4t91RkQmB4Jz9Nl8OpQ2rStUvWxYz=', 10000, 22, 12, 0.00, 99.98, TRUE, TRUE),

-- United States (Ashburn)
('srv_us_ash_01', 'c0000000-0000-0000-0000-000000000030', 'iad1.protonplusvpn.net', '199.180.12.44', 51820,
 'aBc1DeF2GhI3JkL4MnO5PqR6StU7VwX8Yz9AbCdEfGh=', 10000, 28, 16, 0.00, 99.95, TRUE, TRUE),

-- Russia (Moscow)
('srv_ru_mow_01', 'c0000000-0000-0000-0000-000000000001', 'mow1.protonplusvpn.net', '95.173.136.50', 51820,
 'mOw98ZkL21PqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuV=', 10000, 31, 28, 0.10, 99.85, TRUE, TRUE),

-- Russia (Saint Petersburg)
('srv_ru_led_01', 'c0000000-0000-0000-0000-000000000002', 'led1.protonplusvpn.net', '95.173.138.12', 51820,
 'lEd43XyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHi=', 10000, 25, 29, 0.05, 99.88, TRUE, TRUE),

-- Pakistan (Karachi)
('srv_pk_khi_01', 'c0000000-0000-0000-0000-000000000010', 'khi1.protonplusvpn.net', '111.119.160.20', 51820,
 'kHi87ZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJk=', 5000, 42, 38, 0.15, 99.70, FALSE, TRUE),

-- Pakistan (Islamabad)
('srv_pk_isb_01', 'c0000000-0000-0000-0000-000000000011', 'isb1.protonplusvpn.net', '111.119.162.88', 51820,
 'iSb32DeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNo=', 5000, 35, 34, 0.10, 99.75, FALSE, TRUE),

-- Singapore
('srv_sg_sin_01', 'c0000000-0000-0000-0000-000000000060', 'sin1.protonplusvpn.net', '103.253.144.5', 51820,
 'sIn65FgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPq=', 10000, 19, 11, 0.00, 99.99, TRUE, TRUE),

-- Japan (Tokyo)
('srv_jp_tyo_01', 'c0000000-0000-0000-0000-000000000061', 'tyo1.protonplusvpn.net', '133.242.18.90', 51820,
 'tYo14HiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRs=', 10000, 24, 18, 0.00, 99.92, TRUE, TRUE),

-- United Kingdom (London)
('srv_gb_lon_01', 'c0000000-0000-0000-0000-000000000040', 'lon1.protonplusvpn.net', '185.120.48.5', 51820,
 'lOn99LmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVw=', 10000, 33, 15, 0.00, 99.94, TRUE, TRUE),

-- UAE (Dubai)
('srv_ae_dxb_01', 'c0000000-0000-0000-0000-000000000062', 'dxb1.protonplusvpn.net', '185.120.52.88', 51820,
 'dXb77NoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXy=', 10000, 38, 26, 0.10, 99.80, TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;
