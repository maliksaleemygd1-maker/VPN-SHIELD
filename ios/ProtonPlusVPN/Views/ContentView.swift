import SwiftUI
import NetworkExtension

struct ContentView: View {
    @ObservedObject var vpnManager = VPNManager.shared
    @State private var showingLocations = false

    var isConnected: Bool {
        vpnManager.status == .connected
    }

    var isConnecting: Bool {
        vpnManager.status == .connecting
    }

    var body: some View {
        ZStack {
            Color(red: 0.04, green: 0.05, blue: 0.09)
                .ignoresSafeArea()

            VStack(spacing: 24) {
                // Top Header
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Proton Plus VPN")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.white)
                        Text(isConnected ? "Zero-Log Encrypted" : "WireGuard Standby")
                            .font(.system(size: 12))
                            .foregroundColor(isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64) : Color.gray)
                    }
                    Spacer()
                    Text("WireGuard®")
                        .font(.system(size: 11, weight: .semibold))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(Color(red: 0.1, green: 0.13, blue: 0.2))
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding(.horizontal, 24)
                .padding(.top, 16)

                Spacer()

                // Shield Connect Button
                Button(action: {
                    if isConnected {
                        vpnManager.disconnect()
                    } else {
                        vpnManager.connect(serverName: "Frankfurt, Germany", endpointIp: "185.120.44.12")
                    }
                }) {
                    ZStack {
                        Circle()
                            .fill(isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64).opacity(0.15) : Color.clear)
                            .frame(width: 200, height: 200)

                        Circle()
                            .stroke(
                                isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64) : (isConnecting ? Color.yellow : Color.red),
                                lineWidth: 3
                            )
                            .frame(width: 150, height: 150)
                            .background(Circle().fill(Color(red: 0.1, green: 0.13, blue: 0.2)))

                        Image(systemName: isConnected ? "shield.fill" : "power")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 50, height: 50)
                            .foregroundColor(isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64) : (isConnecting ? .yellow : .red))
                    }
                }

                Text(isConnected ? "PROTECTED" : (isConnecting ? "CONNECTING..." : "NOT PROTECTED"))
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64) : (isConnecting ? .yellow : .red))
                    .tracking(2)

                // Speeds
                HStack(spacing: 16) {
                    SpeedBox(title: "DOWNLOAD", value: String(format: "%.1f", vpnManager.downloadMbps), unit: "Mbps", icon: "arrow.down")
                    SpeedBox(title: "UPLOAD", value: String(format: "%.1f", vpnManager.uploadMbps), unit: "Mbps", icon: "arrow.up")
                }
                .padding(.horizontal, 24)

                // Location Card
                HStack {
                    Text("🇩🇪")
                        .font(.system(size: 28))
                    VStack(alignment: .leading, spacing: 2) {
                        Text(vpnManager.currentServerName)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                        Text(isConnected ? "Virtual IP: 10.64.1.2 (Protected)" : "Tap to choose location")
                            .font(.system(size: 12))
                            .foregroundColor(isConnected ? Color(red: 0.0, green: 0.9, blue: 0.64) : Color.gray)
                    }
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color(red: 0.07, green: 0.09, blue: 0.15))
                .cornerRadius(16)
                .padding(.horizontal, 24)
                .onTapGesture {
                    showingLocations = true
                }

                Spacer()
            }
        }
    }
}

struct SpeedBox: View {
    let title: String
    let value: String
    let unit: String
    let icon: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(title)
                    .font(.system(size: 10, weight: .semibold))
                    .foregroundColor(.gray)
                Spacer()
                Image(systemName: icon)
                    .font(.system(size: 12))
                    .foregroundColor(.cyan)
            }
            HStack(alignment: .bottom, spacing: 4) {
                Text(value)
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(.white)
                Text(unit)
                    .font(.system(size: 11))
                    .foregroundColor(.gray)
                    .padding(.bottom, 2)
            }
        }
        .padding()
        .background(Color(red: 0.1, green: 0.13, blue: 0.2))
        .cornerRadius(14)
    }
}
