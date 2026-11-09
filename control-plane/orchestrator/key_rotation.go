package orchestrator

import (
	"crypto/rand"
	"encoding/base64"
	"log"
	"time"

	"golang.org/x/crypto/curve25519"
)

// KeyRotationManager manages scheduled rotation of ephemeral server keys
type KeyRotationManager struct {
	rotationInterval time.Duration
}

func NewKeyRotationManager(interval time.Duration) *KeyRotationManager {
	return &KeyRotationManager{
		rotationInterval: interval,
	}
}

// GenerateEphemeralCurve25519KeyPair produces a cryptographically fresh Curve25519 keypair
func (m *KeyRotationManager) GenerateEphemeralCurve25519KeyPair() (pubKeyB64 string, privKeyB64 string, err error) {
	var priv [32]byte
	if _, err := rand.Read(priv[:]); err != nil {
		return "", "", err
	}

	// Clamp according to Curve25519 specification
	priv[0] &= 248
	priv[31] &= 127
	priv[31] |= 64

	var pub [32]byte
	curve25519.ScalarBaseMult(&pub, &priv)

	pubKeyB64 = base64.StdEncoding.EncodeToString(pub[:])
	privKeyB64 = base64.StdEncoding.EncodeToString(priv[:])
	return pubKeyB64, privKeyB64, nil
}

// LogSecurityAudit logs critical key lifecycle events without leaking private keys
func (m *KeyRotationManager) LogSecurityAudit(event string, nodeID string) {
	log.Printf("[KeyRotationManager] AUDIT EVENT: %s for node: %s at %s", event, nodeID, time.Now().UTC().Format(time.RFC3339))
}
