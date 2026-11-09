# Production Multi-stage Dockerfile for Proton Plus VPN Control Plane
FROM golang:1.22-alpine AS builder

WORKDIR /app
RUN apk add --no-cache git ca-certificates tzdata

COPY backend/go.mod backend/go.sum* ./
RUN go mod download

COPY backend/ ./
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags="-w -s" -o /bin/protonplus-controlplane ./cmd/server

FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /usr/share/zoneinfo /usr/share/zoneinfo
COPY --from=builder /bin/protonplus-controlplane /bin/protonplus-controlplane

USER 10001:10001
EXPOSE 8080 9090

ENTRYPOINT ["/bin/protonplus-controlplane"]
