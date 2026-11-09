package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/protonplusvpn/backend/internal/api"
	"github.com/protonplusvpn/backend/internal/crypto"
	"github.com/protonplusvpn/backend/internal/repository"
)

func main() {
	log.Println("[Proton Plus VPN] Starting Control Plane Server v1.0.0...")

	// 1. Initialize Cryptographic Token Engine (Ed25519)
	tokenManager, err := crypto.NewTokenManager(15*time.Minute, 7*24*time.Hour)
	if err != nil {
		log.Fatalf("Fatal: Failed to initialize token manager: %v", err)
	}

	// 2. Initialize Data Store
	store := repository.NewStore()

	// 3. Initialize HTTP Routing
	router := api.NewRouter(store, tokenManager)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	server := &http.Server{
		Addr:         ":" + port,
		Handler:      router,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	// Graceful shutdown listener
	stopChan := make(chan os.Signal, 1)
	signal.Notify(stopChan, os.Interrupt, syscall.SIGTERM)

	go func() {
		log.Printf("[Proton Plus VPN] Control Plane listening on port %s (TLS 1.3 / HTTP)", port)
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Server listen error: %v", err)
		}
	}()

	<-stopChan
	log.Println("[Proton Plus VPN] Shutting down gracefully...")

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := server.Shutdown(ctx); err != nil {
		log.Printf("Server shutdown warning: %v", err)
	}
	log.Println("[Proton Plus VPN] Control Plane stopped.")
}
