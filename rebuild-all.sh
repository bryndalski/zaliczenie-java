#!/bin/bash
set -e

echo "Stopping containers..."
docker compose down

echo "Removing SecurityConfig files..."
rm -f auth-service/src/main/java/com/microservices/auth/config/SecurityConfig.java
rm -f user-service/src/main/java/com/microservices/user/config/SecurityConfig.java  
rm -f note-service/src/main/java/com/microservices/note/config/SecurityConfig.java

echo "Cleaning Maven cache..."
find . -name "target" -type d -exec rm -rf {} + 2>/dev/null || true

echo "Building Maven projects..."
echo "Building auth-service..."
cd auth-service && mvn clean package -DskipTests && cd ..

echo "Building user-service..."
cd user-service && mvn clean package -DskipTests && cd ..

echo "Building note-service..."
cd note-service && mvn clean package -DskipTests && cd ..

echo "Building all microservice images..."
docker compose build --no-cache

echo "Starting containers with fresh images..."
docker compose up -d --force-recreate --remove-orphans

echo "Done. Use 'docker compose logs -f' to follow logs."
