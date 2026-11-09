package repository

import (
	"errors"
	"fmt"
	"time"

	"github.com/google/uuid"
	"github.com/protonplusvpn/backend/internal/domain"
)

var (
	ErrNoDedicatedIPAvailable = errors.New("no dedicated IP available in requested location")
	ErrAlreadyHasDedicatedIP  = errors.New("user already has an active dedicated IP assignment")
)

func (s *Store) AllocateDedicatedIP(userID, countryCode, cityName string) (*domain.DedicatedIPAssignment, error) {
	s.mu.Lock()
	defer s.mu.Unlock()

	// Check if already assigned
	for _, assignment := range s.dedicatedIPs {
		if assignment.UserID == userID && assignment.Status == "active" {
			return nil, ErrAlreadyHasDedicatedIP
		}
	}

	// Find suitable server supporting dedicated IPs
	var targetServer *domain.VpnServer
	for _, srv := range s.servers {
		if srv.CountryCode == countryCode && srv.SupportsDedicatedIP && srv.IsActive && !srv.DrainMode {
			targetServer = srv
			break
		}
	}

	if targetServer == nil {
		// Fallback to any active server with dedicated IP support
		for _, srv := range s.servers {
			if srv.SupportsDedicatedIP && srv.IsActive && !srv.DrainMode {
				targetServer = srv
				break
			}
		}
	}

	if targetServer == nil {
		return nil, ErrNoDedicatedIPAvailable
	}

	// Generate deterministic stable dedicated IP
	hashVal := len(s.dedicatedIPs) + 1
	dedicatedPublicIP := fmt.Sprintf("199.180.20.%d", 100+hashVal)

	assignment := &domain.DedicatedIPAssignment{
		ID:        uuid.New().String(),
		UserID:    userID,
		ServerID:  targetServer.ID,
		IPAddress: dedicatedPublicIP,
		Status:    "active",
		ExpiresAt: time.Now().UTC().AddDate(0, 1, 0), // 30 days
		CreatedAt: time.Now().UTC(),
	}

	s.dedicatedIPs[assignment.ID] = assignment
	return assignment, nil
}

func (s *Store) GetUserDedicatedIP(userID string) (*domain.DedicatedIPAssignment, bool) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	for _, a := range s.dedicatedIPs {
		if a.UserID == userID && a.Status == "active" {
			return a, true
		}
	}
	return nil, false
}
