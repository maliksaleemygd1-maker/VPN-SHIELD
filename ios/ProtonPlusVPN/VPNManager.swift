import Foundation
import NetworkExtension
import Combine

/// Manages the system VPN configuration via NETunnelProviderManager
public class VPNManager: ObservableObject {
    public static let shared = VPNManager()

    @Published public var status: NEVPNStatus = .disconnected
    @Published public var downloadMbps: Double = 0.0
    @Published public var uploadMbps: Double = 0.0
    @Published public var currentServerName: String = "Frankfurt, Germany"

    private var providerManager: NETunnelProviderManager?
    private var cancellables = Set<AnyCancellable>()

    private init() {
        loadProviderManager()
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(vpnStatusDidChange),
            name: .NEVPNStatusDidChange,
            object: nil
        )
    }

    private func loadProviderManager() {
        NETunnelProviderManager.loadAllFromPreferences { [weak self] managers, error in
            if let manager = managers?.first {
                self?.providerManager = manager
                self?.status = manager.connection.status
            } else {
                let newManager = NETunnelProviderManager()
                newManager.localizedDescription = "Proton Plus VPN"
                let proto = NETunnelProviderProtocol()
                proto.providerBundleIdentifier = "net.protonplusvpn.ios.PacketTunnel"
                proto.serverAddress = "185.120.44.12"
                newManager.protocolConfiguration = proto
                newManager.isEnabled = true
                newManager.saveToPreferences { _ in
                    self?.providerManager = newManager
                }
            }
        }
    }

    @objc private func vpnStatusDidChange(notification: Notification) {
        guard let connection = notification.object as? NEVPNConnection else { return }
        DispatchQueue.main.async {
            self.status = connection.status
            if connection.status == .connected {
                self.downloadMbps = 512.4
                self.uploadMbps = 188.6
            } else {
                self.downloadMbps = 0.0
                self.uploadMbps = 0.0
            }
        }
    }

    public func connect(serverName: String, endpointIp: String) {
        self.currentServerName = serverName
        guard let manager = providerManager else { return }
        do {
            try manager.connection.startVPNTunnel(options: [
                "server_address": endpointIp as NSString,
                "virtual_ipv4": "10.64.1.2" as NSString,
                "mtu": 1420 as NSNumber
            ])
        } catch {
            print("Failed to start VPN tunnel: \(error.localizedDescription)")
        }
    }

    public func disconnect() {
        providerManager?.connection.stopVPNTunnel()
    }
}
