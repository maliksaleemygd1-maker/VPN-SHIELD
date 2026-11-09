package crypto

import (
	"testing"
	"time"
)

func TestArgon2idPasswordHashing(t *testing.T) {
	password := "UltraSecureVpnPass123!"

	hash, err := HashPassword(password)
	if err != nil {
		t.Fatalf("Failed to hash password: %v", err)
	}

	if len(hash) == 0 {
		t.Fatal("Hash result is empty")
	}

	valid, err := VerifyPassword(password, hash)
	if err != nil {
		t.Fatalf("Failed to verify password: %v", err)
	}
	if !valid {
		t.Fatal("Expected password to verify as valid")
	}

	invalid, err := VerifyPassword("WrongPassword123!", hash)
	if err != nil {
		t.Fatalf("Failed to verify wrong password: %v", err)
	}
	if invalid {
		t.Fatal("Expected wrong password to fail verification")
	}
}

func TestEd25519TokenManager(t *testing.T) {
	tm, err := NewTokenManager(5*time.Minute, 1*time.Hour)
	if err != nil {
		t.Fatalf("Failed to create token manager: %v", err)
	}

	token, err := tm.GenerateAccessToken("usr_12345", "test@protonplusvpn.net", "user")
	if err != nil {
		t.Fatalf("Failed to generate token: %v", err)
	}

	claims, err := tm.ValidateAccessToken(token)
	if err != nil {
		t.Fatalf("Failed to validate valid token: %v", err)
	}

	if claims.UserID != "usr_12345" || claims.Email != "test@protonplusvpn.net" {
		t.Fatalf("Unexpected claims data: %+v", claims)
	}

	// Test invalid token
	_, err = tm.ValidateAccessToken(token + "corrupted")
	if err == nil {
		t.Fatal("Expected corrupted token to fail validation")
	}
}
