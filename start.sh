#!/bin/bash

echo "🚀 Starting Microservices Stack (Fast Mode)..."

# Function to check if command exists
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo "❌ Error: $1 is not installed or not in PATH"
        exit 1
    fi
}

# Function to wait for container creation with timeout
wait_for_container_creation() {
    local service_name=$1
    local max_wait=300  # 5 minutes max
    local elapsed=0
    
    echo "⏳ Waiting for $service_name container to be created..."
    while [ $elapsed -lt $max_wait ]; do
        if docker-compose ps $service_name | grep -q $service_name; then
            echo "✅ $service_name container created"
            return 0
        fi
        echo "   Waiting... ${elapsed}s elapsed (creating container)"
        sleep 10
        elapsed=$((elapsed + 10))
    done
    
    echo "❌ $service_name container creation timed out after ${max_wait}s"
    echo "📋 Docker info:"
    docker system df
    echo "📋 Checking if image exists:"
    docker images | grep keycloak || echo "Keycloak image not found locally"
    return 1
}

# Quick service check - just see if it's responding
quick_service_check() {
    local service_name=$1
    local port=$2
    local max_attempts=6  # Only 1 minute max
    local attempt=1
    
    echo "⏳ Quick check for $service_name on port $port..."
    while [ $attempt -le $max_attempts ]; do
        # Try multiple endpoints and methods
        if curl -s -f "http://localhost:$port" > /dev/null 2>&1 || 
           curl -s -f "http://localhost:$port/actuator/health" > /dev/null 2>&1 || 
           curl -s -f "http://localhost:$port/health" > /dev/null 2>&1 ||
           wget -q --spider "http://localhost:$port" 2>/dev/null ||
           wget -q --spider "http://localhost:$port/actuator/health" 2>/dev/null; then
            echo "✅ $service_name is responding"
            return 0
        fi
        echo "   Attempt $attempt/$max_attempts - $service_name not ready..."
        sleep 10
        ((attempt++))
    done
    echo "⚠️  $service_name not responding yet (will continue anyway)"
    return 1
}

# Check prerequisites
echo "🔍 Checking prerequisites..."
check_command docker
check_command docker-compose
check_command curl

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "❌ Error: .env file not found. Please create it with required environment variables."
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Stop any existing containers
echo "📦 Stopping existing containers..."
docker-compose down --remove-orphans

# Clean up any problematic override files temporarily
if [ -f "docker-compose.override.yml" ]; then
    echo "📋 Backing up docker-compose.override.yml to avoid conflicts..."
    mv docker-compose.override.yml docker-compose.override.yml.bak
fi

# Check Docker resources before starting
echo "🔍 Checking Docker resources..."
docker system df
echo ""

# Pre-pull heavy images to avoid timeout during compose up
echo "📥 Pre-pulling container images..."
echo "Pulling Keycloak (this may take a while on first run)..."
docker pull quay.io/keycloak/keycloak:latest &
keycloak_pull_pid=$!

echo "Pulling other images..."
docker pull postgres:15 &
docker pull mongo:7 &
docker pull neo4j:5 &
docker pull nginx:alpine &
docker pull swaggerapi/swagger-ui &

# Wait for Keycloak pull to complete (it's usually the largest)
echo "⏳ Waiting for Keycloak image pull to complete..."
wait $keycloak_pull_pid
if [ $? -eq 0 ]; then
    echo "✅ Keycloak image pulled successfully"
else
    echo "⚠️  Keycloak image pull had issues, continuing anyway..."
fi

# Wait for other pulls
wait
echo "✅ Image pre-pull completed"

# Start infrastructure services first (databases and Keycloak separately)
echo "🏗️ Starting database services first..."
docker-compose up -d keycloak-db mongo-users mongo-passwords neo4j-notes

echo "⏳ Waiting for databases to initialize..."
sleep 15

echo "🔑 Starting Keycloak (may take 2-3 minutes)..."
docker-compose up -d keycloak

# Check for Keycloak startup issues specifically
echo "🔍 Monitoring Keycloak startup..."
sleep 10

# Check if Keycloak container is running and not restarting
if ! docker-compose ps keycloak | grep -q "Up"; then
    echo "❌ Keycloak failed to start properly"
    echo "📋 Keycloak logs:"
    docker-compose logs --tail=20 keycloak
    echo "🔄 Attempting manual Keycloak restart..."
    docker-compose stop keycloak
    docker-compose rm -f keycloak
    sleep 5
    docker-compose up -d keycloak
fi

# Wait specifically for Keycloak container creation
wait_for_container_creation "keycloak" || {
    echo "❌ Keycloak container creation failed"
    echo "📋 Checking Docker logs..."
    docker-compose logs keycloak
    echo "📋 Trying to restart Keycloak..."
    docker-compose restart keycloak
    wait_for_container_creation "keycloak" || {
        echo "❌ Keycloak restart also failed"
        exit 1
    }
}

echo "⏳ Giving Keycloak time to fully start (checking for restart loops)..."
sleep 30

# Check if Keycloak is in a restart loop
restart_count=$(docker inspect --format='{{.RestartCount}}' zaliczenie-java-keycloak-1 2>/dev/null || echo "0")
if [ "$restart_count" -gt 2 ]; then
    echo "⚠️  Keycloak seems to be in a restart loop (restarts: $restart_count)"
    echo "📋 Recent Keycloak logs:"
    docker-compose logs --tail=20 keycloak
    echo "🔄 Attempting Keycloak reset..."
    docker-compose stop keycloak
    docker-compose rm -f keycloak
    echo "⏳ Waiting before restart..."
    sleep 10
    docker-compose up -d keycloak
    sleep 60
fi

echo "🚀 Starting application services..."
docker-compose up -d auth-service user-service note-service swagger-ui

echo "⏳ Waiting for application services..."
sleep 30

echo "🌐 Starting NGINX..."
docker-compose up -d nginx

# Give basic startup time
echo "⏳ Final startup wait..."
sleep 30

# Check service status quickly
echo "📊 Service Status:"
docker-compose ps

# Check for any unhealthy services
echo ""
echo "🔍 Checking for unhealthy services..."
unhealthy_services=$(docker-compose ps --filter "health=unhealthy" --format "table {{.Service}}")
if [ -n "$unhealthy_services" ] && [ "$unhealthy_services" != "SERVICE" ]; then
    echo "⚠️  Found unhealthy services:"
    echo "$unhealthy_services"
    echo "📋 Checking logs for unhealthy services..."
    docker-compose ps --filter "health=unhealthy" --format "{{.Service}}" | while read service; do
        if [ -n "$service" ] && [ "$service" != "SERVICE" ]; then
            echo "--- Logs for $service ---"
            docker-compose logs --tail=10 "$service"
        fi
    done
fi

echo ""
echo "🧪 Quick connectivity tests..."

# Test NGINX first
if curl -s -f "http://localhost/health" > /dev/null 2>&1; then
    echo "✅ NGINX: Ready"
    nginx_ready=true
else
    echo "⚠️  NGINX: Not ready yet"
    nginx_ready=false
fi

# Test alternative NGINX port
if curl -s -f "http://localhost:8080/health" > /dev/null 2>&1; then
    echo "✅ NGINX (8080): Ready"
    nginx_alt_ready=true
else
    echo "⚠️  NGINX (8080): Not ready yet"
    nginx_alt_ready=false
fi

# Test direct service ports
quick_service_check "Auth Service" 8091
quick_service_check "User Service" 8092
quick_service_check "Note Service" 8093

# Additional diagnostic info
echo ""
echo "🔍 Additional Diagnostics:"
echo "Docker containers status:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(zaliczenie|nginx|keycloak|mongo|neo4j)"

echo ""
echo "📊 Resource usage:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}" | head -10

echo ""
echo "🎯 Access Points (Available Now):"
echo "  Main URL:     http://localhost"
echo "  Alternative:  http://localhost:8080"
echo "  Debug Info:   http://localhost/debug"
echo "  Health Check: http://localhost/health"
echo ""
echo "🔧 Direct Service Access:"
echo "  Auth Service:  http://localhost:8091"   # Updated
echo "  User Service:  http://localhost:8092"   # Updated
echo "  Note Service:  http://localhost:8093"   # Updated
echo ""

# Restore override file if it was backed up
if [ -f "docker-compose.override.yml.bak" ]; then
    echo "📋 Restoring docker-compose.override.yml..."
    mv docker-compose.override.yml.bak docker-compose.override.yml
fi

# Final status
if [ "$nginx_ready" = true ] || [ "$nginx_alt_ready" = true ]; then
    echo "🎉 NGINX is working! You can start using the system."
    echo "🌐 Primary access: http://localhost"
    if [ "$nginx_alt_ready" = true ] && [ "$nginx_ready" = false ]; then
        echo "🌐 Use alternative port: http://localhost:8080"
    fi
else
    echo "⚠️  NGINX not responding yet. Try direct service ports above."
    echo ""
    echo "🔧 Quick troubleshooting:"
    echo "  1. Check NGINX logs: docker-compose logs nginx"
    echo "  2. Test direct auth service: curl http://localhost:8091"
    echo "  3. Wait 2-3 more minutes and try again"
fi

echo ""
echo "💡 Services will continue starting in background."
echo "💡 If something doesn't work immediately, wait 2-3 minutes and try again."
echo "💡 Use 'docker-compose logs <service>' to check individual service logs."
echo "💡 Use 'docker-compose ps' to check service health status."
