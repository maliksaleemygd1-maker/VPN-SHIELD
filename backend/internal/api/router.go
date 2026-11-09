package api

import (
	"net/http"

	"github.com/gorilla/mux"
	"github.com/protonplusvpn/backend/internal/api/handlers"
	"github.com/protonplusvpn/backend/internal/crypto"
	"github.com/protonplusvpn/backend/internal/repository"
)

func NewRouter(store *repository.Store, tm *crypto.TokenManager) *mux.Router {
	r := mux.NewRouter()

	authHandler := handlers.NewAuthHandler(store, tm)
	serverHandler := handlers.NewServerHandler(store)
	peerHandler := handlers.NewPeerHandler(store)
	dedicatedHandler := handlers.NewDedicatedIPHandler(store)
	diagHandler := handlers.NewDiagnosticHandler(store)

	// API v1 subrouter
	v1 := r.PathPrefix("/v1").Subrouter()

	// Health check
	r.HandleFunc("/healthz", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"status":"ok","platform":"Proton Plus VPN Control Plane"}`))
	}).Methods(http.MethodGet)

	// Authentication
	v1.HandleFunc("/auth/register", authHandler.Register).Methods(http.MethodPost)
	v1.HandleFunc("/auth/login", authHandler.Login).Methods(http.MethodPost)

	// Server Discovery & Topologies
	v1.HandleFunc("/servers", serverHandler.ListServers).Methods(http.MethodGet)
	v1.HandleFunc("/servers/fastest", serverHandler.GetFastestServer).Methods(http.MethodGet)
	v1.HandleFunc("/servers/lowest-latency", serverHandler.GetLowestLatencyServer).Methods(http.MethodGet)
	v1.HandleFunc("/servers/lowest-load", serverHandler.GetLowestLoadServer).Methods(http.MethodGet)
	v1.HandleFunc("/servers/country/{code}", serverHandler.GetServersByCountry).Methods(http.MethodGet)
	v1.HandleFunc("/servers/city/{name}", serverHandler.GetServersByCity).Methods(http.MethodGet)
	v1.HandleFunc("/countries", serverHandler.ListCountries).Methods(http.MethodGet)

	// WireGuard Peer Lifecycle
	v1.HandleFunc("/vpn/peer/register", peerHandler.RegisterPeer).Methods(http.MethodPost)
	v1.HandleFunc("/vpn/peer/disconnect", peerHandler.DisconnectPeer).Methods(http.MethodPost)

	// Dedicated IP Assignment
	v1.HandleFunc("/dedicated-ip/allocate", dedicatedHandler.Allocate).Methods(http.MethodPost)
	v1.HandleFunc("/dedicated-ip/status", dedicatedHandler.GetStatus).Methods(http.MethodGet)

	// Diagnostics & Leak Tests
	v1.HandleFunc("/diagnostics/leak-test", diagHandler.LeakTest).Methods(http.MethodGet)

	return r
}
