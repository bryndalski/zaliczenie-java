#!/bin/bash

echo "🔍 Microservices Connection Diagnostic Script"
echo "=============================================="

# Function to test HTTP endpoint
test_endpoint() {
    local url=$1
    local name=$2
    echo -n "Testing $name ($url): "
    
    if curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 "$url" 2>/dev/null | grep -q "200\|301\|302"; then
        echo "✅ SUCCESS"
        return 0
    else
        echo "❌ FAILED"
        return 1
    fi
}

echo
echo "1. 🐳 Checking Docker containers..."
docker-compose ps

echo
echo "2. 🔍 Checking port mappings..."
docker port $(docker-compose ps -q nginx) 2>/dev/null || echo "❌ No nginx container found"

echo
echo "3. 🌐 Testing internal NGINX..."
if docker-compose exec -T nginx curl -s -o /dev/null -w "%{http_code}" http://localhost 2>/dev/null | grep -q "200\|301\|302"; then
    echo "✅ NGINX internal test: SUCCESS"
else
    echo "❌ NGINX internal test: FAILED"
    echo "   NGINX logs:"
    docker-compose logs nginx | tail -5
fi

echo
echo "4. 🔗 Testing host connections..."
test_endpoint "http://localhost" "localhost"
test_endpoint "http://127.0.0.1" "127.0.0.1"
test_endpoint "http://0.0.0.0" "0.0.0.0"

echo
echo "5. 🏥 Testing alternative port 8080..."
test_endpoint "http://localhost:8080" "localhost:8080"

echo
echo "6. 🔍 Checking what's using port 80..."
if command -v lsof >/dev/null; then
    lsof -i :80 2>/dev/null || echo "Port 80 appears to be free"
elif command -v netstat >/dev/null; then
    netstat -tlnp 2>/dev/null | grep :80 || echo "Port 80 appears to be free"
else
    echo "Cannot check port usage (no lsof or netstat)"
fi

echo
echo "7. 🧪 Testing backend services from within nginx..."
if docker-compose exec -T nginx curl -s -o /dev/null -w "%{http_code}" http://auth-service:8080 2>/dev/null | grep -q "200\|404"; then
    echo "✅ auth-service: reachable"
else
    echo "❌ auth-service: unreachable"
fi

if docker-compose exec -T nginx curl -s -o /dev/null -w "%{http_code}" http://user-service:8080 2>/dev/null | grep -q "200\|404"; then
    echo "✅ user-service: reachable"
else
    echo "❌ user-service: unreachable"
fi

if docker-compose exec -T nginx curl -s -o /dev/null -w "%{http_code}" http://note-service:8080 2>/dev/null | grep -q "200\|404"; then
    echo "✅ note-service: reachable"
else
    echo "❌ note-service: unreachable"
fi

echo
echo "8. 💡 Recommendations:"
echo "   - If localhost fails but 127.0.0.1 works: DNS issue"
echo "   - If internal NGINX works but external fails: Port binding issue"
echo "   - If backend services unreachable: Service startup issue"
echo "   - If port 80 busy: Use alternative port 8080"

echo
echo "🚀 Quick fixes to try:"
echo "   1. docker-compose restart nginx"
echo "   2. Change port to 8080 in docker-compose.yml"
echo "   3. docker-compose down && docker-compose up --build"
