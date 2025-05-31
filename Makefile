.PHONY: build up down clean logs swagger

build:
	cd auth-service && mvn clean package -DskipTests
	cd user-service && mvn clean package -DskipTests
	cd note-service && mvn clean package -DskipTests

up:
	docker-compose up --build

down:
	docker-compose down

clean:
	docker-compose down -v
	docker system prune -f

logs:
	docker-compose logs -f

auth-logs:
	docker-compose logs -f auth-service

user-logs:
	docker-compose logs -f user-service

note-logs:
	docker-compose logs -f note-service

swagger:
	@echo "🚀 Opening Swagger UI..."
	@echo "Auth Service API: http://localhost/swagger-ui/"
	@echo "API Docs available at:"
	@echo "  - Auth Service: http://localhost/auth/v3/api-docs"
	@echo "  - User Service: http://localhost/users/v3/api-docs"
	@echo "  - Note Service: http://localhost/notes/v3/api-docs"
	@echo "  - GraphQL Playground: http://localhost/graphql"

setup-keycloak:
	@echo "🔐 Setting up Keycloak realm and client..."
	@echo "Please configure Keycloak manually at http://localhost/keycloak"
	@echo "1. Create realm: microservices-realm"
	@echo "2. Create client: microservices-client"
	@echo "3. Create roles: admin, user, editor, observer"
	@echo "4. Create test users and assign roles"

setup: build up setup-keycloak swagger
	@echo "✅ Setup complete! Access services at:"
	@echo "  - Swagger UI: http://localhost/swagger-ui/"
	@echo "  - Keycloak Admin: http://localhost/keycloak"
	@echo "  - All APIs available through: http://localhost/"

test-api:
	@echo "🧪 Testing API endpoints..."
	@echo "1. First register a user:"
	@echo "   curl -X POST http://localhost/users/register -H 'Content-Type: application/json' -d '{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"firstName\":\"Test\",\"lastName\":\"User\"}'"
	@echo ""
	@echo "2. Then login to get JWT token:"
	@echo "   curl -X POST http://localhost/auth/login -H 'Content-Type: application/json' -d '{\"username\":\"testuser\",\"password\":\"password123\"}'"
	@echo ""
	@echo "3. Use token in subsequent requests:"
	@echo "   curl -X GET http://localhost/auth/me -H 'Authorization: Bearer YOUR_TOKEN_HERE'"

# Diagnostic commands for troubleshooting
diagnose:
	@echo "🔍 Docker System Information:"
	docker system df
	@echo ""
	@echo "🔍 Available Resources:"
	docker system info | grep -E "(Total Memory|CPUs)"
	@echo ""
	@echo "🔍 Running Containers:"
	docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Size}}"

keycloak-logs:
	docker-compose logs -f keycloak

keycloak-debug:
	@echo "🔍 Keycloak Container Status:"
	docker-compose ps keycloak
	@echo ""
	@echo "🔍 Keycloak Resource Usage:"
	docker stats --no-stream keycloak* || echo "Keycloak not running"
	@echo ""
	@echo "🔍 Keycloak Logs (last 50 lines):"
	docker-compose logs --tail=50 keycloak

keycloak-restart:
	@echo "🔄 Restarting Keycloak..."
	docker-compose stop keycloak
	docker-compose rm -f keycloak
	docker-compose up -d keycloak

keycloak-reset:
	@echo "🗑️  Resetting Keycloak completely..."
	docker-compose stop keycloak
	docker-compose rm -f keycloak
	docker volume rm zaliczenie-java_keycloak_data || echo "Volume not found"
	docker-compose up -d keycloak-db
	sleep 10
	docker-compose up -d keycloak

keycloak-fix:
	@echo "🔧 Fixing Keycloak configuration issues..."
	docker-compose down
	@echo "📋 Removing problematic override temporarily..."
	@if [ -f "docker-compose.override.yml" ]; then \
		mv docker-compose.override.yml docker-compose.override.yml.disabled; \
		echo "Disabled docker-compose.override.yml"; \
	fi
	docker-compose up -d keycloak-db
	sleep 15
	docker-compose up -d keycloak
	@echo "✅ Keycloak should now start without --auto-build errors"
	@echo "⏳ Wait 2-3 minutes for Keycloak to fully initialize"

keycloak-logs-live:
	@echo "📋 Watching Keycloak logs (Ctrl+C to stop)..."
	docker-compose logs -f keycloak

force-rebuild:
	docker-compose down -v
	docker system prune -f
	docker-compose build --no-cache
	docker-compose up --build

pull-images:
	@echo "📥 Pre-pulling all images..."
	docker pull quay.io/keycloak/keycloak:latest
	docker pull postgres:15
	docker pull mongo:7
	docker pull neo4j:5
	docker pull nginx:alpine
	docker pull swaggerapi/swagger-ui
	
help:
	@echo "Available commands:"
	@echo "  build              - Build all services"
	@echo "  up                 - Start all services"
	@echo "  down               - Stop all services"
	@echo "  clean              - Clean everything including volumes"
	@echo "  clear-ports        - Kill processes using ports 80, 443, 8080"
	@echo "  start-clean        - Clear ports and run start.sh"
	@echo "  logs               - View all logs"
	@echo "  swagger            - Open Swagger UI"
	@echo "  setup-keycloak     - Set up Keycloak realm and client"
	@echo "  setup              - Build, up, setup-keycloak, and swagger"
	@echo "  test-api           - Print test API commands"
	@echo "  diagnose            - Show Docker system information and running containers"
	@echo "  keycloak-logs      - View Keycloak logs"
	@echo "  keycloak-debug     - Debug Keycloak container"
	@echo "  keycloak-restart   - Restart Keycloak"
	@echo "  keycloak-reset     - Reset Keycloak data"
	@echo "  keycloak-fix       - Fix Keycloak configuration issues"
	@echo "  keycloak-logs-live - Tail Keycloak logs"
	@echo "  force-rebuild      - Rebuild services from scratch"
	@echo "  pull-images        - Pre-pull all container images"
	@echo "  help               - Show this help message"
	@echo "  keycloak-test      - Test Keycloak connectivity"
	@echo "  keycloak-force-fix - Force fix Keycloak issues"

keycloak-test:
	@echo "🧪 Testing Keycloak connectivity..."
	@echo "Direct access test:"
	@curl -s -I http://localhost:8080 || echo "❌ Direct access failed"
	@echo ""
	@echo "Health endpoint test:"
	@curl -s -I http://localhost:8080/health/ready || echo "❌ Health endpoint not ready"
	@echo ""
	@echo "Admin console test:"
	@curl -s -I http://localhost:8080/admin || echo "❌ Admin console not accessible"

keycloak-force-fix:
	@echo "🔧 Force fixing Keycloak issues..."
	docker-compose stop keycloak
	docker-compose rm -f keycloak
	@echo "Clearing any conflicting containers..."
	docker ps -a | grep keycloak | awk '{print $$1}' | xargs -r docker rm -f
	@echo "Starting fresh Keycloak..."
	docker-compose up -d keycloak-db
	sleep 10
	docker-compose up -d keycloak
	@echo "✅ Keycloak restart initiated. Wait 2-3 minutes then run 'make keycloak-test'"

clear-ports:
	@echo "🔫 Clearing ports 80, 443, and 8080..."
	@if command -v lsof >/dev/null 2>&1; then \
		echo "Using lsof to find and kill processes..."; \
		lsof -ti :80 2>/dev/null | xargs -r kill -9 || echo "Port 80 clear"; \
		lsof -ti :443 2>/dev/null | xargs -r kill -9 || echo "Port 443 clear"; \
		lsof -ti :8080 2>/dev/null | xargs -r kill -9 || echo "Port 8080 clear"; \
	elif command -v netstat >/dev/null 2>&1; then \
		echo "Using netstat to find processes..."; \
		netstat -tlnp 2>/dev/null | grep ":80 " | awk '{print $$7}' | cut -d'/' -f1 | grep -v - | xargs -r kill -9 || echo "Port 80 clear"; \
		netstat -tlnp 2>/dev/null | grep ":443 " | awk '{print $$7}' | cut -d'/' -f1 | grep -v - | xargs -r kill -9 || echo "Port 443 clear"; \
		netstat -tlnp 2>/dev/null | grep ":8080 " | awk '{print $$7}' | cut -d'/' -f1 | grep -v - | xargs -r kill -9 || echo "Port 8080 clear"; \
	else \
		echo "❌ Cannot clear ports (no lsof or netstat available)"; \
	fi
	@echo "✅ Port clearing completed"

start-clean: clear-ports
	@echo "🚀 Starting with clean ports..."
	./start.sh
