package repository

import (
	"testing"
)

func TestServerScoringAndRanking(t *testing.T) {
	store := NewStore()

	servers := store.GetAllServers()
	if len(servers) == 0 {
		t.Fatal("Expected active servers, got 0")
	}

	fastest, ok := store.GetFastestServer()
	if !ok || fastest == nil {
		t.Fatal("Expected fastest server to be selected")
	}

	// Score of fastest should be less than or equal to subsequent
	for _, s := range servers {
		if s.Score < fastest.Score {
			t.Fatalf("Server %s has better score (%f) than fastest (%f)", s.ID, s.Score, fastest.Score)
		}
	}
}

func TestDedicatedIPAllocation(t *testing.T) {
	store := NewStore()

	assignment, err := store.AllocateDedicatedIP("user_test_1", "US", "Ashburn (VA)")
	if err != nil {
		t.Fatalf("Failed to allocate dedicated IP: %v", err)
	}

	if assignment.IPAddress == "" || assignment.Status != "active" {
		t.Fatalf("Invalid assignment: %+v", assignment)
	}

	// Verify retrieval
	retrieved, ok := store.GetUserDedicatedIP("user_test_1")
	if !ok || retrieved.ID != assignment.ID {
		t.Fatalf("Failed to retrieve user dedicated IP assignment")
	}

	// Verify duplicate allocation error
	_, err = store.AllocateDedicatedIP("user_test_1", "DE", "Frankfurt")
	if err != ErrAlreadyHasDedicatedIP {
		t.Fatalf("Expected ErrAlreadyHasDedicatedIP, got: %v", err)
	}
}

func TestWireGuardPeerRegistration(t *testing.T) {
	store := NewStore()

	pubKey := "TestClientWireGuardPublicKey12345678901234567="
	resp, err := store.RegisterPeer("srv_de_fra_01", pubKey, "Pixel 9 Pro", "android")
	if err != nil {
		t.Fatalf("Failed to register peer: %v", err)
	}

	if resp.AssignedInternalIPv4 == "" || resp.ServerPublicKey == "" || resp.MTU != 1420 {
		t.Fatalf("Invalid peer response: %+v", resp)
	}
}
