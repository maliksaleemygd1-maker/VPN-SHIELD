// Proton Plus VPN - Manifest V3 Background Service Worker
// Enforces strict WebRTC leak protection and secure proxy tunneling

const DEFAULT_SERVER = {
  host: "fra1.protonplusvpn.net",
  port: 443,
  scheme: "https"
};

// 1. Prevent WebRTC STUN leaks: Disables non-proxied UDP traffic
function enforceWebRtcProtection() {
  if (chrome.privacy && chrome.privacy.network && chrome.privacy.network.webRTCIPHandlingPolicy) {
    chrome.privacy.network.webRTCIPHandlingPolicy.set({
      value: "disable_non_proxied_udp"
    }, () => {
      console.log("[ProtonPlus Extension] WebRTC non-proxied UDP leak prevention ACTIVE.");
    });
  }
}

// 2. Configure Secure Browser Proxy
function setProxyConnection(enabled, server = DEFAULT_SERVER) {
  if (!enabled) {
    chrome.proxy.settings.set({ value: { mode: "direct" }, scope: "regular" }, () => {
      console.log("[ProtonPlus Extension] Direct connection restored.");
      chrome.storage.local.set({ connected: false });
    });
    return;
  }

  const config = {
    mode: "fixed_servers",
    rules: {
      singleProxy: {
        scheme: server.scheme,
        host: server.host,
        port: parseInt(server.port, 10)
      },
      bypassList: ["localhost", "127.0.0.1", "*.local"]
    }
  };

  chrome.proxy.settings.set({ value: config, scope: "regular" }, () => {
    console.log(`[ProtonPlus Extension] Connected to ${server.host}:${server.port}`);
    chrome.storage.local.set({ connected: true, activeServer: server });
  });
}

// Listen for messages from popup UI
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === "CONNECT") {
    enforceWebRtcProtection();
    setProxyConnection(true, request.server || DEFAULT_SERVER);
    sendResponse({ status: "connected" });
  } else if (request.action === "DISCONNECT") {
    setProxyConnection(false);
    sendResponse({ status: "disconnected" });
  } else if (request.action === "GET_STATUS") {
    chrome.storage.local.get(["connected", "activeServer"], (result) => {
      sendResponse(result);
    });
    return true; // Keep channel open for async response
  }
});

chrome.runtime.onInstalled.addListener(() => {
  enforceWebRtcProtection();
  chrome.storage.local.set({ connected: false });
});
