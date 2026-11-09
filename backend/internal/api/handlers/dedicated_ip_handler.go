package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/protonplusvpn/backend/internal/repository"
)

type DedicatedIPHandler struct {
	store *repository.Store
}

func NewDedicatedIPHandler(store *repository.Store) *DedicatedIPHandler {
	return &DedicatedIPHandler{store: store}
}

type AllocateDedicatedIPRequest struct {
	UserID      string `json:"user_id"`
	CountryCode string `json:"country_code"`
	CityName    string `json:"city_name"`
}

func (h *DedicatedIPHandler) Allocate(w http.ResponseWriter, r *http.Request) {
	var req AllocateDedicatedIPRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request payload"}`, http.StatusBadRequest)
		return
	}

	if req.UserID == "" {
		req.UserID = "default_user"
	}

	assignment, err := h.store.AllocateDedicatedIP(req.UserID, req.CountryCode, req.CityName)
	if err != nil {
		http.Error(w, `{"error":"`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(assignment)
}

func (h *DedicatedIPHandler) GetStatus(w http.ResponseWriter, r *http.Request) {
	userID := r.URL.Query().Get("user_id")
	if userID == "" {
		userID = "default_user"
	}

	assignment, ok := h.store.GetUserDedicatedIP(userID)
	if !ok {
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(map[string]interface{}{
			"has_dedicated_ip": false,
		})
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"has_dedicated_ip": true,
		"assignment":       assignment,
	})
}
