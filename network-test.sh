#!/bin/bash

# Network Testing Script for Microservices
# ----------------------------------------

echo "========================================"
echo "🔍 Microservices Network Test"
echo "========================================"

# Check if Docker and Docker Compose are installed
echo -e "\n📋 Checking Docker installation..."
if command -v docker &> /dev/null; then
    echo "✅ Docker is installed"
    docker --version
else
    echo "❌ Docker is not installed"
    exit 1
fi

if command -v docker-compose &> /dev/null; then
    echo "✅ Docker Compose is installed"
    docker-compose --version
else
    echo "❌ Docker Compose is not installed"
    exit 1
fi

# Check if containers are running
echo -e "\n📋 Checking containers status..."
docker-compose ps

# Check if network exists
echo -e "\n📋 Checking Docker networks..."
docker network ls | grep microservices-network

# Test NGINX
echo -e "\n📋 Testing NGINX..."
echo "- Testing port 80:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost || echo "❌ Failed to connect to NGINX on port 80"

# Test individual services
echo -e "\n📋 Testing direct service access..."

echo "- Testing Auth Service (port 8091):"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8091/actuator/health || echo "❌ Failed to connect to Auth Service"

echo "- Testing User Service (port 8092):"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8092/actuator/health || echo "❌ Failed to connect to User Service"

echo "- Testing Note Service (port 8093):"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8093/actuator/health || echo "❌ Failed to connect to Note Service"

echo "- Testing Keycloak (port 8095):"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8095 || echo "❌ Failed to connect to Keycloak"

# Test NGINX proxying
echo -e "\n📋 Testing NGINX proxying..."

echo "- Testing /auth/ endpoint:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/auth/ || echo "❌ Failed to access /auth/ endpoint"

echo "- Testing /users/ endpoint:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/users/ || echo "❌ Failed to access /users/ endpoint"

echo "- Testing /notes/ endpoint:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/notes/ || echo "❌ Failed to access /notes/ endpoint"

echo "- Testing /swagger-ui/ endpoint:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/swagger-ui/ || echo "❌ Failed to access /swagger-ui/ endpoint"

echo "- Testing /keycloak/ endpoint:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/keycloak/ || echo "❌ Failed to access /keycloak/ endpoint"

echo -e "\n📋 Testing NGINX debug endpoints..."

echo "- Testing /health endpoint:"
curl -s http://localhost/health || echo "❌ Failed to access /health endpoint"

echo -e "\n- Testing /status endpoint:"
curl -s http://localhost/status || echo "❌ Failed to access /status endpoint"

echo -e "\n- Testing /debug endpoint:"
curl -s http://localhost/debug || echo "❌ Failed to access /debug endpoint"

echo -e "\n========================================"
echo "🏁 Network test complete"
echo "========================================"
