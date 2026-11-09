variable "aws_region" {
  description = "AWS deployment region"
  type        = "string"
  default     = "eu-central-1"
}

variable "node_location" {
  description = "Human-readable edge node city identifier"
  type        = "string"
  default     = "Frankfurt"
}

variable "instance_type" {
  description = "Network-optimized compute instance"
  type        = "string"
  default     = "c6in.xlarge"
}

variable "ami_id" {
  description = "Ubuntu 24.04 LTS AMI with 6.8+ Linux Kernel"
  type        = "string"
  default     = "ami-0faab6bdbac9486fb"
}
