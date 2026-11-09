package domain

import (
	"time"
)

// User represents an authenticated account
type User struct {
	ID           string    `json:"id"`
	Email        string    `json:"email"`
	PasswordHash string    `json:"-"`
	Role         string    `json:"role"` // "user", "admin", "netops", "support"
	IsActive     bool      `json:"is_active"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}

// Plan represents a subscription tier
type Plan struct {
	ID                string `json:"id"`
	Name              string `json:"name"`
	MaxDevices        int    `json:"max_devices"`
	AllowsDedicatedIP bool   `json:"allows_dedicated_ip"`
	MaxBandwidthMbps  int    `json:"max_bandwidth_mbps"`
}

// Subscription tracks entitlement status
type Subscription struct {
	ID        string    `json:"id"`
	UserID    string    `json:"user_id"`
	PlanID    string    `json:"plan_id"`
	Status    string    `json:"status"` // "active", "expired", "canceled"
	ExpiresAt time.Time `json:"expires_at"`
}

// Device represents a client endpoint with its hardware public key
type Device struct {
	ID                 string     `json:"id"`
	UserID             string     `json:"user_id"`
	DeviceName         string     `json:"device_name"`
	Platform           string     `json:"platform"` // "android", "ios", "extension"
	WireGuardPublicKey string     `json:"wireguard_public_key"`
	IsRevoked          bool       `json:"is_revoked"`
	LastSeenAt         *time.Time `json:"last_seen_at,omitempty"`
	CreatedAt          time.Time  `json:"created_at"`
}

// Country represents a supported geographical territory
type Country struct {
	Code      string `json:"code"`
	Name      string `json:"name"`
	FlagEmoji string `json:"flag_emoji"`
	IsActive  bool   `json:"is_active"`
}

// City represents a regional metropolitan presence
type City struct {
	ID          string  `json:"id"`
	CountryCode string  `json:"country_code"`
	Name        string  `json:"name"`
	Latitude    float64 `json:"latitude"`
	Longitude   float64 `json:"longitude"`
	IsActive    bool    `json:"is_active"`
}

// VpnServer represents an in-service WireGuard hardware node
type VpnServer struct {
	ID                         string    `json:"id"`
	CityID                     string    `json:"city_id"`
	CountryCode                string    `json:"country_code"`
	CountryName                string    `json:"country_name"`
	CityName                   string    `json:"city_name"`
	Hostname                   string    `json:"hostname"`
	EndpointIP                 string    `json:"endpoint_ip"`
	Port                       int       `json:"port"`
	PublicKey                  string    `json:"public_key"`
	CapacityMbps               int       `json:"capacity_mbps"`
	CurrentLoadPercent         int       `json:"current_load_percent"`
	AvgLatencyMs               int       `json:"avg_latency_ms"`
	PacketLossPercent          float64   `json:"packet_loss_percent"`
	HistoricalReliabilityScore float64   `json:"historical_reliability_score"`
	SupportsDedicatedIP        bool      `json:"supports_dedicated_ip"`
	DrainMode                  bool      `json:"drain_mode"`
	IsActive                   bool      `json:"is_active"`
	LastHeartbeatAt            time.Time `json:"last_heartbeat_at"`
	Score                      float64   `json:"score,omitempty"` // Computed ranking score
}

// VpnPeer records a currently active device tunnel on a server
type VpnPeer struct {
	ID              string     `json:"id"`
	ServerID        string     `json:"server_id"`
	DeviceID        string     `json:"device_id"`
	ClientPublicKey string     `json:"client_public_key"`
	AssignedIPv4    string     `json:"assigned_ipv4"`
	AssignedIPv6    string     `json:"assigned_ipv6,omitempty"`
	IsActive        bool       `json:"is_active"`
	ConnectedAt     time.Time  `json:"connected_at"`
	LastHandshakeAt *time.Time `json:"last_handshake_at,omitempty"`
}

// DedicatedIPAssignment records a fixed public IP assigned exclusively to a user
type DedicatedIPAssignment struct {
	ID        string    `json:"id"`
	UserID    string    `json:"user_id"`
	DeviceID  *string   `json:"device_id,omitempty"`
	ServerID  string    `json:"server_id"`
	IPAddress string    `json:"ip_address"`
	Status    string    `json:"status"` // "active", "quarantine", "released"
	ExpiresAt time.Time `json:"expires_at"`
	CreatedAt time.Time `json:"created_at"`
}

// RegisterPeerResponse is returned when a client registers its public key
type RegisterPeerResponse struct {
	AssignedInternalIPv4 string   `json:"assigned_internal_ipv4"`
	AssignedInternalIPv6 string   `json:"assigned_internal_ipv6,omitempty"`
	ServerPublicKey      string   `json:"server_public_key"`
	ServerEndpoint       string   `json:"server_endpoint"`
	DNSServers           []string `json:"dns_servers"`
	AllowedIPs           []string `json:"allowed_ips"`
	MTU                  int      `json:"mtu"`
}

// LeakTestResponse gives diagnostic proof of VPN tunnel and DNS isolation
type LeakTestResponse struct {
	EgressIP             string `json:"egress_ip"`
	IsVpnIP              bool   `json:"is_vpn_ip"`
	VpnServerID          string `json:"vpn_server_id,omitempty"`
	DetectedDNSResolver  string `json:"detected_dns_resolver"`
	DNSLeakDetected      bool   `json:"dns_leak_detected"`
	IPv6LeakDetected     bool   `json:"ipv6_leak_detected"`
	WebRTCLeakProtected  bool   `json:"webrtc_leak_protected"`
}
