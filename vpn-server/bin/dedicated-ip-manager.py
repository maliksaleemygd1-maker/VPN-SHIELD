#!/usr/bin/env python3
"""
Proton Plus VPN - Dedicated IP Lifecycle & Quarantine Manager
Handles 24h quarantine periods after subscription expiration to prevent IP reputation contamination.
"""

import json
import os
import sys
import time

QUARANTINE_HOURS = 24

class DedicatedIPPoolManager:
    def __init__(self, state_file="/tmp/dedicated_ip_pool.json"):
        self.state_file = state_file
        self.pool = self._load()

    def _load(self):
        if os.path.exists(self.state_file):
            try:
                with open(self.state_file, "r") as f:
                    return json.load(f)
            except Exception:
                pass
        return {
            "available": ["199.180.20.102", "199.180.20.103", "199.180.20.104"],
            "assigned": {
                "199.180.20.101": {
                    "user_id": "usr_premium_01",
                    "assigned_at": int(time.time()),
                    "expires_at": int(time.time()) + 2505600
                }
            },
            "quarantine": {}
        }

    def _save(self):
        with open(self.state_file, "w") as f:
            json.dump(self.pool, f, indent=2)

    def release_to_quarantine(self, ip_addr):
        if ip_addr in self.pool["assigned"]:
            info = self.pool["assigned"].pop(ip_addr)
            self.pool["quarantine"][ip_addr] = {
                "user_id": info["user_id"],
                "quarantined_at": int(time.time()),
                "available_at": int(time.time()) + (QUARANTINE_HOURS * 3600)
            }
            self._save()
            print(f"[+] IP {ip_addr} placed into {QUARANTINE_HOURS}h quarantine.")

    def process_expired_quarantine(self):
        now = int(time.time())
        cleared = []
        for ip, data in list(self.pool["quarantine"].items()):
            if now >= data["available_at"]:
                del self.pool["quarantine"][ip]
                self.pool["available"].append(ip)
                cleared.append(ip)
        if cleared:
            self._save()
            print(f"[+] Cleared {len(cleared)} IPs from quarantine back to available pool: {cleared}")

if __name__ == "__main__":
    mgr = DedicatedIPPoolManager()
    mgr.process_expired_quarantine()
    print("[+] Dedicated IP Quarantine Manager checked successfully.")
