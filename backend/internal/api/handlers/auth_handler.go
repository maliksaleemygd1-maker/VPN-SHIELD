package handlers

import (
	"encoding/json"
	"net/http"
	"strings"
	"time"

	"github.com/google/uuid"
	"github.com/protonplusvpn/backend/internal/crypto"
	"github.com/protonplusvpn/backend/internal/domain"
	"github.com/protonplusvpn/backend/internal/repository"
)

type AuthHandler struct {
	store *repository.Store
	tm    *crypto.TokenManager
}

func NewAuthHandler(store *repository.Store, tm *crypto.TokenManager) *AuthHandler {
	return &AuthHandler{store: store, tm: tm}
}

type RegisterRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type AuthResponse struct {
	UserID       string `json:"user_id"`
	Email        string `json:"email"`
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	ExpiresIn    int    `json:"expires_in"`
}

func (h *AuthHandler) Register(w http.ResponseWriter, r *http.Request) {
	var req RegisterRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request payload"}`, http.StatusBadRequest)
		return
	}

	req.Email = strings.TrimSpace(strings.ToLower(req.Email))
	if len(req.Email) < 5 || !strings.Contains(req.Email, "@") {
		http.Error(w, `{"error":"invalid email address"}`, http.StatusBadRequest)
		return
	}
	if len(req.Password) < 8 {
		http.Error(w, `{"error":"password must be at least 8 characters"}`, http.StatusBadRequest)
		return
	}

	hash, err := crypto.HashPassword(req.Password)
	if err != nil {
		http.Error(w, `{"error":"internal crypto error"}`, http.StatusInternalServerError)
		return
	}

	userID := uuid.New().String()
	user := &domain.User{
		ID:           userID,
		Email:        req.Email,
		PasswordHash: hash,
		Role:         "user",
		IsActive:     true,
		CreatedAt:    time.Now().UTC(),
		UpdatedAt:    time.Now().UTC(),
	}

	// Register in store
	// (In a full DB, INSERT INTO users ...)
	token, err := h.tm.GenerateAccessToken(user.ID, user.Email, user.Role)
	if err != nil {
		http.Error(w, `{"error":"token generation failed"}`, http.StatusInternalServerError)
		return
	}

	refreshToken, _, _ := h.tm.GenerateRefreshToken()

	resp := AuthResponse{
		UserID:       user.ID,
		Email:        user.Email,
		AccessToken:  token,
		RefreshToken: refreshToken,
		ExpiresIn:    900,
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(resp)
}

func (h *AuthHandler) Login(w http.ResponseWriter, r *http.Request) {
	var req RegisterRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request payload"}`, http.StatusBadRequest)
		return
	}

	req.Email = strings.TrimSpace(strings.ToLower(req.Email))
	// Mock/Demo or real user validation
	userID := "usr_" + uuid.New().String()[:8]
	token, err := h.tm.GenerateAccessToken(userID, req.Email, "user")
	if err != nil {
		http.Error(w, `{"error":"token generation failed"}`, http.StatusInternalServerError)
		return
	}

	refreshToken, _, _ := h.tm.GenerateRefreshToken()

	resp := AuthResponse{
		UserID:       userID,
		Email:        req.Email,
		AccessToken:  token,
		RefreshToken: refreshToken,
		ExpiresIn:    900,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}
