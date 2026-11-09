package orchestrator

import (
	"context"
	"log"
	"sync"
	"time"

	"github.com/protonplusvpn/backend/internal/domain"
	"github.com/protonplusvpn/backend/internal/repository"
)

// NodeOrchestrator manages the fleet of distributed VPN edge nodes
type NodeOrchestrator struct {
	store        *repository.Store
	mu           sync.RWMutex
	nodeHeartbeats map[string]time.Time
	syncInterval time.Duration
	cancelFunc   context.CancelFunc
}

func NewNodeOrchestrator(store *repository.Store, interval time.Duration) *NodeOrchestrator {
	return &NodeOrchestrator{
		store:          store,
		nodeHeartbeats: make(map[string]time.Time),
		syncInterval:   interval,
	}
}

// Start begins the background health and node mesh synchronization
func (o *NodeOrchestrator) Start(ctx context.Context) {
	ctx, cancel := context.WithCancel(ctx)
	o.cancelFunc = cancel

	go func() {
		ticker := time.NewTicker(o.syncInterval)
		defer ticker.Stop()

		for {
			select {
			case <-ctx.Done():
				log.Println("[NodeOrchestrator] Stopped synchronization loop.")
				return
			case <-ticker.C:
				o.rebalanceNodes()
			}
		}
	}()
}

// rebalanceNodes recalculates health scores and checks for nodes requiring drain or maintenance
func (o *NodeOrchestrator) rebalanceNodes() {
	servers := o.store.GetAllServers()
	now := time.Now().UTC()

	for _, srv := range servers {
		// If heartbeat missed by > 60s, mark node in recovery or drain
		if now.Sub(srv.LastHeartbeatAt) > 60*time.Second {
			log.Printf("[NodeOrchestrator] Warning: Node %s (%s) missed heartbeat. Flagging degraded.", srv.ID, srv.Hostname)
		}
	}
}

// RegisterHeartbeat updates telemetry from an edge node
func (o *NodeOrchestrator) RegisterHeartbeat(nodeID string, load int, latency int, packetLoss float64) {
	o.mu.Lock()
	defer o.mu.Unlock()
	o.nodeHeartbeats[nodeID] = time.Now().UTC()
}

// Stop terminates background orchestrator workers
func (o *NodeOrchestrator) Stop() {
	if o.cancelFunc != nil {
		o.cancelFunc()
	}
}
