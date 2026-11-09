# Terraform Multi-Cloud Infrastructure for Proton Plus VPN Global Edge Nodes
terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# High-Performance WireGuard Edge Node Instance
resource "aws_instance" "vpn_edge_node" {
  ami           = var.ami_id
  instance_type = var.instance_type # e.g. c6in.xlarge with 12.5 Gbps network bandwidth

  associate_public_ip_address = true
  source_dest_check           = false # Required for VPN NAT & forwarding

  vpc_security_group_ids = [aws_security_group.vpn_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              set -euo pipefail
              apt-get update && apt-get install -y wireguard-tools nftables unbound python3
              echo "net.core.default_qdisc=fq" >> /etc/sysctl.conf
              echo "net.ipv4.tcp_congestion_control=bbr" >> /etc/sysctl.conf
              echo "net.ipv4.ip_forward=1" >> /etc/sysctl.conf
              echo "net.ipv6.conf.all.forwarding=1" >> /etc/sysctl.conf
              sysctl -p
              systemctl enable unbound
              systemctl start unbound
              EOF

  tags = {
    Name        = "ProtonPlus-Edge-${var.node_location}"
    Environment = "production"
    Role        = "wireguard-data-plane"
  }
}

resource "aws_security_group" "vpn_sg" {
  name        = "protonplus-vpn-sg"
  description = "Minimal surface: allows incoming WireGuard UDP and management SSH"

  # WireGuard UDP Ingress
  ingress {
    description = "WireGuard Tunnel"
    from_port   = 51820
    to_port     = 51820
    protocol    = "udp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Obfuscated Stealth WireGuard TLS
  ingress {
    description = "Stealth TLS Obfuscation"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Outbound WAN traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
