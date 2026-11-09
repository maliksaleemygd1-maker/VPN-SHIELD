document.addEventListener("DOMContentLoaded", () => {
  const connectBtn = document.getElementById("connectBtn");
  const statusLabel = document.getElementById("statusLabel");
  const virtualIp = document.getElementById("virtualIp");

  let isConnected = false;

  chrome.runtime.sendMessage({ action: "GET_STATUS" }, (response) => {
    if (response && response.connected) {
      updateUI(true);
    } else {
      updateUI(false);
    }
  });

  connectBtn.addEventListener("click", () => {
    if (isConnected) {
      chrome.runtime.sendMessage({ action: "DISCONNECT" }, () => {
        updateUI(false);
      });
    } else {
      chrome.runtime.sendMessage({ action: "CONNECT" }, () => {
        updateUI(true);
      });
    }
  });

  function updateUI(connected) {
    isConnected = connected;
    if (connected) {
      connectBtn.classList.add("connected");
      statusLabel.textContent = "PROTECTED";
      statusLabel.classList.add("connected");
      virtualIp.textContent = "Virtual IP: 10.64.1.2 (Protected)";
      virtualIp.style.color = "#00E5A3";
    } else {
      connectBtn.classList.remove("connected");
      statusLabel.textContent = "NOT PROTECTED";
      statusLabel.classList.remove("connected");
      virtualIp.textContent = "Endpoint: 185.120.44.12";
      virtualIp.style.color = "#64748B";
    }
  }
});
