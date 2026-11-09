package repository

import (
	"sort"
	"strings"

	"github.com/protonplusvpn/backend/internal/domain"
)

// CalculateServerScore computes a real-time health and performance score (lower is better)
// Formula: Score = (Latency * 0.45) + (Load * 0.35) + (PacketLoss * 50 * 0.15) + ((100 - Reliability) * 0.05)
func CalculateServerScore(s *domain.VpnServer) float64 {
	latencyWeight := float64(s.AvgLatencyMs) * 0.45
	loadWeight := float64(s.CurrentLoadPercent) * 0.35
	lossWeight := (s.PacketLossPercent * 50.0) * 0.15
	reliabilityWeight := (100.0 - s.HistoricalReliabilityScore) * 0.05

	return latencyWeight + loadWeight + lossWeight + reliabilityWeight
}

func (s *Store) GetAllServers() []domain.VpnServer {
	s.mu.RLock()
	defer s.mu.RUnlock()

	result := make([]domain.VpnServer, 0, len(s.servers))
	for _, srv := range s.servers {
		if srv.IsActive && !srv.DrainMode {
			c := *srv
			c.Score = CalculateServerScore(&c)
			result = append(result, c)
		}
	}

	sort.Slice(result, func(i, j int) bool {
		return result[i].Score < result[j].Score
	})

	return result
}

func (s *Store) GetServerByID(id string) (*domain.VpnServer, bool) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	srv, ok := s.servers[id]
	if !ok || !srv.IsActive {
		return nil, false
	}
	c := *srv
	c.Score = CalculateServerScore(&c)
	return &c, true
}

func (s *Store) GetServersByCountry(countryCode string) []domain.VpnServer {
	s.mu.RLock()
	defer s.mu.RUnlock()

	cc := strings.ToUpper(countryCode)
	result := make([]domain.VpnServer, 0)
	for _, srv := range s.servers {
		if srv.CountryCode == cc && srv.IsActive && !srv.DrainMode {
			c := *srv
			c.Score = CalculateServerScore(&c)
			result = append(result, c)
		}
	}

	sort.Slice(result, func(i, j int) bool {
		return result[i].Score < result[j].Score
	})

	return result
}

func (s *Store) GetServersByCity(cityName string) []domain.VpnServer {
	s.mu.RLock()
	defer s.mu.RUnlock()

	q := strings.ToLower(cityName)
	result := make([]domain.VpnServer, 0)
	for _, srv := range s.servers {
		if strings.Contains(strings.ToLower(srv.CityName), q) && srv.IsActive && !srv.DrainMode {
			c := *srv
			c.Score = CalculateServerScore(&c)
			result = append(result, c)
		}
	}

	sort.Slice(result, func(i, j int) bool {
		return result[i].Score < result[j].Score
	})

	return result
}

func (s *Store) GetFastestServer() (*domain.VpnServer, bool) {
	servers := s.GetAllServers()
	if len(servers) == 0 {
		return nil, false
	}
	return &servers[0], true
}

func (s *Store) GetLowestLatencyServer() (*domain.VpnServer, bool) {
	servers := s.GetAllServers()
	if len(servers) == 0 {
		return nil, false
	}

	sort.Slice(servers, func(i, j int) bool {
		return servers[i].AvgLatencyMs < servers[j].AvgLatencyMs
	})

	return &servers[0], true
}

func (s *Store) GetLowestLoadServer() (*domain.VpnServer, bool) {
	servers := s.GetAllServers()
	if len(servers) == 0 {
		return nil, false
	}

	sort.Slice(servers, func(i, j int) bool {
		return servers[i].CurrentLoadPercent < servers[j].CurrentLoadPercent
	})

	return &servers[0], true
}

func (s *Store) GetAllCountries() []domain.Country {
	s.mu.RLock()
	defer s.mu.RUnlock()

	res := make([]domain.Country, 0, len(s.countries))
	for _, c := range s.countries {
		if c.IsActive {
			res = append(res, *c)
		}
	}
	sort.Slice(res, func(i, j int) bool {
		return res[i].Name < res[j].Name
	})
	return res
}
