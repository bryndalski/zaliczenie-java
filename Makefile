.PHONY: build up down clean logs

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

setup-keycloak:
	@echo "Setting up Keycloak realm and client..."
	@echo "Please configure Keycloak manually at http://localhost/keycloak"
	@echo "1. Create realm: microservices-realm"
	@echo "2. Create client: microservices-client"
	@echo "3. Create roles: admin, user, editor, observer"
