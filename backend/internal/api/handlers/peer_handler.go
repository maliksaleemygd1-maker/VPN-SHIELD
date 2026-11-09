package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/protonplusvpn/backend/internal/repository"
)

type PeerHandler struct {
	store *repository.Store
}

func NewPeerHandler(store *repository.Store) *PeerHandler {
	return &PeerHandler{store: store}
}

type RegisterPeerRequest struct {
	ServerID        string `json:"server_id"`
	ClientPublicKey string `json:"client_public_key"`
	DeviceName      string `json:"device_name"`
	Platform        string `json:"platform"`
}

type DisconnectPeerRequest struct {
	ServerID        string `json:"server_id"`
	ClientPublicKey string `json:"client_public_key"`
}

func (h *PeerHandler) RegisterPeer(w http.ResponseWriter, r *http.Request) {
	var req RegisterPeerRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request payload"}`, http.StatusBadRequest)
		return
	}

	if req.ServerID == "" || req.ClientPublicKey == "" {
		http.Error(w, `{"error":"server_id and client_public_key are mandatory"}`, http.StatusBadRequest)
		return
	}

	resp, err := h.store.RegisterPeer(req.ServerID, req.ClientPublicKey, req.DeviceName, req.Platform)
	if err != nil {
		http.Error(w, `{"error":"`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

func (h *PeerHandler) DisconnectPeer(w http.ResponseWriter, r *http.Request) {
	var req DisconnectPeerRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request payload"}`, http.StatusBadRequest)
		return
	}

	removed := h.store.RemovePeer(req.ServerID, req.ClientPublicKey)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"success": removed,
		"status":  "disconnected",
	})
}
