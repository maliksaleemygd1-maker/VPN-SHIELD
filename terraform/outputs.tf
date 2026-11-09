output "edge_node_public_ip" {
  description = "Public WAN IPv4 of the provisioned WireGuard edge node"
  value       = aws_instance.vpn_edge_node.public_ip
}

output "wireguard_port" {
  description = "WireGuard UDP port"
  value       = 51820
}
