output "eks_cluster_name" {
  value = aws_eks_cluster.k8s.name
}

output "eks_cluster_endpoint" {
  value = aws_eks_cluster.k8s.endpoint
}

output "eks_cluster_ca_certificate" {
  value = aws_eks_cluster.k8s.certificate_authority[0].data
}

output "redis_endpoint" {
  description = "The primary endpoint address of the Redis cluster"
  value       = aws_elasticache_cluster.redis.cache_nodes[0].address
}

