package repository

import (
	"errors"
	"fmt"
	"time"

	"github.com/google/uuid"
	"github.com/protonplusvpn/backend/internal/domain"
)

var (
	ErrServerNotFound = errors.New("specified vpn server does not exist or is inactive")
	ErrPeerCollision  = errors.New("client public key is already registered on this node")
)

// RegisterPeer binds a client's Curve25519 public key to a virtual IP on the node
func (s *Store) RegisterPeer(serverID, clientPublicKey, deviceName, platform string) (*domain.RegisterPeerResponse, error) {
	s.mu.Lock()
	defer s.mu.Unlock()

	srv, ok := s.servers[serverID]
	if !ok || !srv.IsActive || srv.DrainMode {
		return nil, ErrServerNotFound
	}

	peerKey := fmt.Sprintf("%s:%s", serverID, clientPublicKey)
	vip := s.NextVirtualIP()

	peer := &domain.VpnPeer{
		ID:              uuid.New().String(),
		ServerID:        serverID,
		DeviceID:        uuid.New().String(),
		ClientPublicKey: clientPublicKey,
		AssignedIPv4:    vip,
		AssignedIPv6:    fmt.Sprintf("fd00:64::%x", s.nextIPIndex),
		IsActive:        true,
		ConnectedAt:     time.Now().UTC(),
	}
	s.peers[peerKey] = peer

	response := &domain.RegisterPeerResponse{
		AssignedInternalIPv4: fmt.Sprintf("%s/32", vip),
		AssignedInternalIPv6: fmt.Sprintf("%s/128", peer.AssignedIPv6),
		ServerPublicKey:      srv.PublicKey,
		ServerEndpoint:       fmt.Sprintf("%s:%d", srv.EndpointIP, srv.Port),
		DNSServers:           []string{"10.64.0.1", "fd00::1"},
		AllowedIPs:           []string{"0.0.0.0/0", "::/0"},
		MTU:                  1420,
	}

	return response, nil
}

// RemovePeer removes a peer session from the memory table
func (s *Store) RemovePeer(serverID, clientPublicKey string) bool {
	s.mu.Lock()
	defer s.mu.Unlock()

	peerKey := fmt.Sprintf("%s:%s", serverID, clientPublicKey)
	if _, ok := s.peers[peerKey]; ok {
		delete(s.peers, peerKey)
		return true
	}
	return false
}
