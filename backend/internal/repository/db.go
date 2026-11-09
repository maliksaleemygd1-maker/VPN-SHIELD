package repository

import (
	"context"
	"fmt"
	"log"
	"sync"
	"time"

	"github.com/protonplusvpn/backend/internal/domain"
)

// Store encapsulates the data access layer
type Store struct {
	mu           sync.RWMutex
	users        map[string]*domain.User
	devices      map[string]*domain.Device
	countries    map[string]*domain.Country
	cities       map[string]*domain.City
	servers      map[string]*domain.VpnServer
	peers        map[string]*domain.VpnPeer
	dedicatedIPs map[string]*domain.DedicatedIPAssignment
	nextIPIndex  uint32
}

// NewInMemoryStore provides a memory-safe, thread-safe repository implementation
func NewStore() *Store {
	s := &Store{
		users:        make(map[string]*domain.User),
		devices:      make(map[string]*domain.Device),
		countries:    make(map[string]*domain.Country),
		cities:       make(map[string]*domain.City),
		servers:      make(map[string]*domain.VpnServer),
		peers:        make(map[string]*domain.VpnPeer),
		dedicatedIPs: make(map[string]*domain.DedicatedIPAssignment),
		nextIPIndex:  10,
	}
	s.seedDefaultData()
	return s
}

func (s *Store) seedDefaultData() {
	s.mu.Lock()
	defer s.mu.Unlock()

	// Countries
	countries := []domain.Country{
		{Code: "PK", Name: "Pakistan", FlagEmoji: "🇵🇰", IsActive: true},
		{Code: "RU", Name: "Russia", FlagEmoji: "🇷🇺", IsActive: true},
		{Code: "US", Name: "United States", FlagEmoji: "🇺🇸", IsActive: true},
		{Code: "DE", Name: "Germany", FlagEmoji: "🇩🇪", IsActive: true},
		{Code: "GB", Name: "United Kingdom", FlagEmoji: "🇬🇧", IsActive: true},
		{Code: "SG", Name: "Singapore", FlagEmoji: "🇸🇬", IsActive: true},
		{Code: "JP", Name: "Japan", FlagEmoji: "🇯🇵", IsActive: true},
		{Code: "AE", Name: "United Arab Emirates", FlagEmoji: "🇦🇪", IsActive: true},
		{Code: "NL", Name: "Netherlands", FlagEmoji: "🇳🇱", IsActive: true},
		{Code: "FR", Name: "France", FlagEmoji: "🇫🇷", IsActive: true},
		{Code: "CH", Name: "Switzerland", FlagEmoji: "🇨🇭", IsActive: true},
		{Code: "CA", Name: "Canada", FlagEmoji: "🇨🇦", IsActive: true},
		{Code: "AU", Name: "Australia", FlagEmoji: "🇦🇺", IsActive: true},
		{Code: "IN", Name: "India", FlagEmoji: "🇮🇳", IsActive: true},
	}
	for _, c := range countries {
		s.countries[c.Code] = &c
	}

	// Cities
	cities := []domain.City{
		{ID: "c_mow", CountryCode: "RU", Name: "Moscow", Latitude: 55.7558, Longitude: 37.6173, IsActive: true},
		{ID: "c_led", CountryCode: "RU", Name: "Saint Petersburg", Latitude: 59.9343, Longitude: 30.3351, IsActive: true},
		{ID: "c_khi", CountryCode: "PK", Name: "Karachi", Latitude: 24.8607, Longitude: 67.0011, IsActive: true},
		{ID: "c_isb", CountryCode: "PK", Name: "Islamabad", Latitude: 33.6844, Longitude: 73.0479, IsActive: true},
		{ID: "c_fra", CountryCode: "DE", Name: "Frankfurt", Latitude: 50.1109, Longitude: 8.6821, IsActive: true},
		{ID: "c_ash", CountryCode: "US", Name: "Ashburn (VA)", Latitude: 39.0438, Longitude: -77.4874, IsActive: true},
		{ID: "c_sin", CountryCode: "SG", Name: "Singapore", Latitude: 1.3521, Longitude: 103.8198, IsActive: true},
		{ID: "c_tyo", CountryCode: "JP", Name: "Tokyo", Latitude: 35.6762, Longitude: 139.6503, IsActive: true},
		{ID: "c_lon", CountryCode: "GB", Name: "London", Latitude: 51.5074, Longitude: -0.1278, IsActive: true},
		{ID: "c_dxb", CountryCode: "AE", Name: "Dubai", Latitude: 25.2048, Longitude: 55.2708, IsActive: true},
	}
	for _, city := range cities {
		s.cities[city.ID] = &city
	}

	// High-Throughput VPN Servers
	servers := []domain.VpnServer{
		{
			ID: "srv_de_fra_01", CityID: "c_fra", CountryCode: "DE", CountryName: "Germany", CityName: "Frankfurt",
			Hostname: "fra1.protonplusvpn.net", EndpointIP: "185.120.44.12", Port: 51820,
			PublicKey: "x5G0zWbF7Yw0vO4t91RkQmB4Jz9Nl8OpQ2rStUvWxYz=",
			CapacityMbps: 10000, CurrentLoadPercent: 22, AvgLatencyMs: 12, PacketLossPercent: 0.0,
			HistoricalReliabilityScore: 99.98, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_us_ash_01", CityID: "c_ash", CountryCode: "US", CountryName: "United States", CityName: "Ashburn (VA)",
			Hostname: "iad1.protonplusvpn.net", EndpointIP: "199.180.12.44", Port: 51820,
			PublicKey: "aBc1DeF2GhI3JkL4MnO5PqR6StU7VwX8Yz9AbCdEfGh=",
			CapacityMbps: 10000, CurrentLoadPercent: 28, AvgLatencyMs: 16, PacketLossPercent: 0.0,
			HistoricalReliabilityScore: 99.95, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_ru_mow_01", CityID: "c_mow", CountryCode: "RU", CountryName: "Russia", CityName: "Moscow",
			Hostname: "mow1.protonplusvpn.net", EndpointIP: "95.173.136.50", Port: 51820,
			PublicKey: "mOw98ZkL21PqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuV=",
			CapacityMbps: 10000, CurrentLoadPercent: 31, AvgLatencyMs: 28, PacketLossPercent: 0.1,
			HistoricalReliabilityScore: 99.85, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_ru_led_01", CityID: "c_led", CountryCode: "RU", CountryName: "Russia", CityName: "Saint Petersburg",
			Hostname: "led1.protonplusvpn.net", EndpointIP: "95.173.138.12", Port: 51820,
			PublicKey: "lEd43XyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHi=",
			CapacityMbps: 10000, CurrentLoadPercent: 25, AvgLatencyMs: 29, PacketLossPercent: 0.05,
			HistoricalReliabilityScore: 99.88, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_pk_khi_01", CityID: "c_khi", CountryCode: "PK", CountryName: "Pakistan", CityName: "Karachi",
			Hostname: "khi1.protonplusvpn.net", EndpointIP: "111.119.160.20", Port: 51820,
			PublicKey: "kHi87ZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJk=",
			CapacityMbps: 5000, CurrentLoadPercent: 42, AvgLatencyMs: 38, PacketLossPercent: 0.15,
			HistoricalReliabilityScore: 99.70, SupportsDedicatedIP: false, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_pk_isb_01", CityID: "c_isb", CountryCode: "PK", CountryName: "Pakistan", CityName: "Islamabad",
			Hostname: "isb1.protonplusvpn.net", EndpointIP: "111.119.162.88", Port: 51820,
			PublicKey: "iSb32DeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNo=",
			CapacityMbps: 5000, CurrentLoadPercent: 35, AvgLatencyMs: 34, PacketLossPercent: 0.10,
			HistoricalReliabilityScore: 99.75, SupportsDedicatedIP: false, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_sg_sin_01", CityID: "c_sin", CountryCode: "SG", CountryName: "Singapore", CityName: "Singapore",
			Hostname: "sin1.protonplusvpn.net", EndpointIP: "103.253.144.5", Port: 51820,
			PublicKey: "sIn65FgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPq=",
			CapacityMbps: 10000, CurrentLoadPercent: 19, AvgLatencyMs: 11, PacketLossPercent: 0.0,
			HistoricalReliabilityScore: 99.99, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_jp_tyo_01", CityID: "c_tyo", CountryCode: "JP", CountryName: "Japan", CityName: "Tokyo",
			Hostname: "tyo1.protonplusvpn.net", EndpointIP: "133.242.18.90", Port: 51820,
			PublicKey: "tYo14HiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRs=",
			CapacityMbps: 10000, CurrentLoadPercent: 24, AvgLatencyMs: 18, PacketLossPercent: 0.0,
			HistoricalReliabilityScore: 99.92, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_gb_lon_01", CityID: "c_lon", CountryCode: "GB", CountryName: "United Kingdom", CityName: "London",
			Hostname: "lon1.protonplusvpn.net", EndpointIP: "185.120.48.5", Port: 51820,
			PublicKey: "lOn99LmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVw=",
			CapacityMbps: 10000, CurrentLoadPercent: 33, AvgLatencyMs: 15, PacketLossPercent: 0.0,
			HistoricalReliabilityScore: 99.94, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
		{
			ID: "srv_ae_dxb_01", CityID: "c_dxb", CountryCode: "AE", CountryName: "United Arab Emirates", CityName: "Dubai",
			Hostname: "dxb1.protonplusvpn.net", EndpointIP: "185.120.52.88", Port: 51820,
			PublicKey: "dXb77NoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXy=",
			CapacityMbps: 10000, CurrentLoadPercent: 38, AvgLatencyMs: 26, PacketLossPercent: 0.10,
			HistoricalReliabilityScore: 99.80, SupportsDedicatedIP: true, IsActive: true, LastHeartbeatAt: time.Now(),
		},
	}

	for _, srv := range servers {
		s.servers[srv.ID] = &srv
	}

	log.Printf("[Repository] Initialized with %d countries, %d cities, and %d active VPN nodes",
		len(s.countries), len(s.cities), len(s.servers))
}

func (s *Store) NextVirtualIP() string {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.nextIPIndex++
	b3 := (s.nextIPIndex >> 8) & 0xFF
	b4 := s.nextIPIndex & 0xFF
	return fmt.Sprintf("10.64.%d.%d", b3, b4)
}

func (s *Store) Ping(ctx context.Context) error {
	return nil
}
