package crypto

import (
	"crypto/ed25519"
	"crypto/rand"
	"crypto/sha256"
	"encoding/base64"
	"encoding/json"
	"errors"
	"fmt"
	"strings"
	"time"
)

var (
	ErrInvalidToken = errors.New("invalid or expired token")
)

type TokenClaims struct {
	UserID    string    `json:"sub"`
	Email     string    `json:"email"`
	Role      string    `json:"role"`
	ExpiresAt time.Time `json:"exp"`
	IssuedAt  time.Time `json:"iat"`
}

type TokenManager struct {
	publicKey  ed25519.PublicKey
	privateKey ed25519.PrivateKey
	accessTTL  time.Duration
	refreshTTL time.Duration
}

func NewTokenManager(accessTTL, refreshTTL time.Duration) (*TokenManager, error) {
	pub, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		return nil, err
	}
	return &TokenManager{
		publicKey:  pub,
		privateKey: priv,
		accessTTL:  accessTTL,
		refreshTTL: refreshTTL,
	}, nil
}

// GenerateAccessToken signs an Ed25519 payload
func (tm *TokenManager) GenerateAccessToken(userID, email, role string) (string, error) {
	now := time.Now().UTC()
	claims := TokenClaims{
		UserID:    userID,
		Email:     email,
		Role:      role,
		IssuedAt:  now,
		ExpiresAt: now.Add(tm.accessTTL),
	}

	payloadBytes, err := json.Marshal(claims)
	if err != nil {
		return "", err
	}

	sig := ed25519.Sign(tm.privateKey, payloadBytes)
	encodedPayload := base64.RawURLEncoding.EncodeToString(payloadBytes)
	encodedSig := base64.RawURLEncoding.EncodeToString(sig)

	return fmt.Sprintf("v4.public.%s.%s", encodedPayload, encodedSig), nil
}

// GenerateRefreshToken creates a cryptographic random token and returns its hash for DB storage
func (tm *TokenManager) GenerateRefreshToken() (token string, hash string, err error) {
	b := make([]byte, 32)
	if _, err := rand.Read(b); err != nil {
		return "", "", err
	}
	token = base64.RawURLEncoding.EncodeToString(b)
	sum := sha256.Sum256([]byte(token))
	hash = base64.RawURLEncoding.EncodeToString(sum[:])
	return token, hash, nil
}

// ValidateAccessToken parses and verifies the Ed25519 signature and TTL
func (tm *TokenManager) ValidateAccessToken(tokenStr string) (*TokenClaims, error) {
	parts := strings.Split(tokenStr, ".")
	if len(parts) != 4 || parts[0] != "v4" || parts[1] != "public" {
		return nil, ErrInvalidToken
	}

	payloadBytes, err := base64.RawURLEncoding.DecodeString(parts[2])
	if err != nil {
		return nil, ErrInvalidToken
	}

	sig, err := base64.RawURLEncoding.DecodeString(parts[3])
	if err != nil {
		return nil, ErrInvalidToken
	}

	if !ed25519.Verify(tm.publicKey, payloadBytes, sig) {
		return nil, ErrInvalidToken
	}

	var claims TokenClaims
	if err := json.Unmarshal(payloadBytes, &claims); err != nil {
		return nil, ErrInvalidToken
	}

	if time.Now().UTC().After(claims.ExpiresAt) {
		return nil, errors.New("token expired")
	}

	return &claims, nil
}
