package handlers

import (
	"encoding/json"
	"net/http"
	"strings"
	"time"

	"github.com/gorilla/mux"
	"github.com/protonplusvpn/backend/internal/repository"
)

type ServerHandler struct {
	store *repository.Store
}

func NewServerHandler(store *repository.Store) *ServerHandler {
	return &ServerHandler{store: store}
}

type ServerListResponse struct {
	UpdatedAt int64       `json:"updated_at"`
	Count     int         `json:"count"`
	Servers   interface{} `json:"servers"`
}

func (h *ServerHandler) ListServers(w http.ResponseWriter, r *http.Request) {
	servers := h.store.GetAllServers()

	resp := ServerListResponse{
		UpdatedAt: time.Now().Unix(),
		Count:     len(servers),
		Servers:   servers,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

func (h *ServerHandler) GetFastestServer(w http.ResponseWriter, r *http.Request) {
	server, ok := h.store.GetFastestServer()
	if !ok {
		http.Error(w, `{"error":"no available servers"}`, http.StatusServiceUnavailable)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(server)
}

func (h *ServerHandler) GetLowestLatencyServer(w http.ResponseWriter, r *http.Request) {
	server, ok := h.store.GetLowestLatencyServer()
	if !ok {
		http.Error(w, `{"error":"no available servers"}`, http.StatusServiceUnavailable)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(server)
}

func (h *ServerHandler) GetLowestLoadServer(w http.ResponseWriter, r *http.Request) {
	server, ok := h.store.GetLowestLoadServer()
	if !ok {
		http.Error(w, `{"error":"no available servers"}`, http.StatusServiceUnavailable)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(server)
}

func (h *ServerHandler) ListCountries(w http.ResponseWriter, r *http.Request) {
	countries := h.store.GetAllCountries()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"count":     len(countries),
		"countries": countries,
	})
}

func (h *ServerHandler) GetServersByCountry(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	code := vars["code"]

	servers := h.store.GetServersByCountry(code)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"country_code": strings.ToUpper(code),
		"count":        len(servers),
		"servers":      servers,
	})
}

func (h *ServerHandler) GetServersByCity(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	name := vars["name"]

	servers := h.store.GetServersByCity(name)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"city":    name,
		"count":   len(servers),
		"servers": servers,
	})
}
