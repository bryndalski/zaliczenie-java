# Microservices Backend - Java Spring Boot

A complete microservices architecture built with Java Spring Boot, featuring authentication, user management, and note-taking capabilities with graph relationships.

## 🚀 Quick Access - Port 80

**All services are accessible through NGINX on port 80:**

| Service | URL | Description |
|---------|-----|-------------|
| **Main Entry** | http://localhost | Redirects to Swagger UI |
| **Swagger UI** | http://localhost/swagger-ui/ | 📊 API Documentation & Testing |
| **Auth API** | http://localhost/auth/ | 🔐 Authentication endpoints |
| **User API** | http://localhost/users/ | 👤 User management endpoints |
| **Note API** | http://localhost/notes/ | 📝 Note management endpoints |
| **GraphQL** | http://localhost/graphql | 🔍 GraphQL playground |
| **Keycloak Admin** | http://localhost/keycloak/ | 🔑 Identity management |

> **Note**: Only NGINX exposes port 80 to the host. All other services run on internal Docker network.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                            NGINX                                │
│                    (Reverse Proxy & Load Balancer)             │
│                         Port 80/443                            │
└─────────────────────┬───────────────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┬─────────────┬─────────────┐
        │             │             │             │             │
   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐
   │  Auth   │   │  User   │   │  Note   │   │ Swagger │   │Keycloak │
   │Service  │   │Service  │   │Service  │   │   UI    │   │         │
   │:8080    │   │:8080    │   │:8080    │   │:8080    │   │:8080    │
   └────┬────┘   └────┬────┘   └────┬────┘   └─────────┘   └─────────┘
        │             │             │
   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐
   │  Mongo  │   │  Mongo  │   │  Neo4j  │
   │Password │   │  Users  │   │  Notes  │
   │   DB    │   │   DB    │   │   DB    │
   └─────────┘   └─────────┘   └─────────┘
```

## 🚀 Services

### 🔐 Auth Service
- **Purpose**: JWT authentication and authorization via Keycloak
- **Database**: MongoDB (mongo-passwords)
- **Port**: Internal 8080
- **Endpoints**:
  - `POST /auth/login` - User authentication
  - `POST /auth/logout` - User logout
  - `POST /auth/refresh` - Token refresh
  - `POST /auth/reset-password` - Password reset
  - `GET /auth/me` - Current user info

### 👤 User Service
- **Purpose**: User profile and account management
- **Databases**: 
  - MongoDB (mongo-users) - User profiles
  - MongoDB (mongo-passwords) - Password hashes
- **Port**: Internal 8080
- **Endpoints**:
  - `POST /users/register` - User registration
  - `GET /users/{id}` - Get user profile
  - `PUT /users/{id}` - Update user profile
  - `DELETE /users/{id}` - Delete user account

### 📝 Note Service
- **Purpose**: Note management with graph relationships
- **Database**: Neo4j (neo4j-notes)
- **Port**: Internal 8080
- **APIs**: REST + GraphQL
- **Endpoints**:
  - `POST /notes` - Create note
  - `GET /notes/{id}` - Get note
  - `PUT /notes/{id}` - Update note
  - `DELETE /notes/{id}` - Delete note
  - `POST /notes/share` - Share note with permissions
  - `GET /graphql` - GraphQL endpoint

### 🔑 Keycloak
- **Purpose**: Identity and Access Management
- **Features**: JWT tokens, roles, realms
- **Roles**: admin, user, editor, observer
- **Admin Access**: `/keycloak/admin`

### 📊 Swagger UI
- **Purpose**: API documentation and testing
- **Access**: `/swagger-ui/`
- **Features**: Interactive API testing for all services

## 🛠️ Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17 (for local development)
- Maven 3.8+ (for local development)

### 1. Clone and Setup
```bash
git clone <repository>
cd zaliczenie-java
```

### 2. Build Services
```bash
make build
# or manually:
# cd auth-service && mvn clean package -DskipTests
# cd user-service && mvn clean package -DskipTests
# cd note-service && mvn clean package -DskipTests
```

### 3. Start All Services
```bash
make up
# or: docker-compose up --build
```

### 4. Configure Keycloak (First Time Setup)
1. Visit: **http://localhost/keycloak** (Note: port 80, not 8080!)
2. Login with admin credentials (see .env file)
3. Create realm: `microservices-realm`
4. Create client: `microservices-client`
5. Configure client settings:
   - Client Protocol: `openid-connect`
   - Access Type: `confidential`
   - Valid Redirect URIs: `*`
   - Web Origins: `*`
6. Create roles: `admin`, `user`, `editor`, `observer`
7. Create test users and assign roles

### 5. Start Testing!

**🎯 Immediate Access:**
```bash
# Open your browser to:
open http://localhost/swagger-ui/

# Or test with curl:
curl http://localhost/auth/login
```

## 📖 API Documentation

### Authentication Flow
1. **Register User**: `POST /users/register`
2. **Login**: `POST /auth/login` → Returns JWT token
3. **Use Token**: Add `Authorization: Bearer <token>` header to requests
4. **Refresh**: `POST /auth/refresh` when token expires

### Example API Calls

#### 1. Register User
```bash
curl -X POST http://localhost/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### 3. Create Note (with JWT token)
```bash
curl -X POST http://localhost/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "title": "My First Note",
    "content": "This is the content of my note"
  }'
```

#### 4. GraphQL Query
```bash
curl -X POST http://localhost/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "query": "query { notes { id title content linkedNotes { id title } } }"
  }'
```

## 🗃️ Database Schemas

### MongoDB Users Collection
```json
{
  "_id": "ObjectId",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### MongoDB Passwords Collection
```json
{
  "_id": "ObjectId",
  "userId": "ObjectId",
  "hashedPassword": "string",
  "salt": "string",
  "createdAt": "datetime"
}
```

### Neo4j Note Nodes
```cypher
CREATE (n:Note {
  id: "uuid",
  title: "string",
  content: "string",
  ownerId: "string",
  createdAt: "datetime",
  updatedAt: "datetime"
})

CREATE (n1:Note)-[:LINKED_TO]->(n2:Note)
CREATE (n:Note)-[:SHARED_WITH {role: "EDITOR"}]->(u:User)
```

## 🔒 Security & Permissions

### JWT Token Structure
- **Issuer**: Keycloak
- **Algorithm**: RS256
- **Claims**: sub, roles, email, preferred_username
- **Expiry**: 15 minutes (configurable)

### Permission Roles
- **admin**: Full system access
- **user**: Basic user operations
- **editor**: Can edit shared content
- **observer**: Read-only access

### Note Sharing Permissions
- **OWNER**: Full control (read, write, delete, share)
- **EDITOR**: Read and write access
- **OBSERVER**: Read-only access

## 🐳 Docker Services

| Service | Image | Internal Port | External Port | Network |
|---------|-------|---------------|---------------|---------|
| **nginx** | nginx:alpine | 80 | **80, 8080** | microservices-network |
| auth-service | Custom (Spring Boot) | **8081** | 8091 | microservices-network |
| user-service | Custom (Spring Boot) | **8082** | 8092 | microservices-network |
| note-service | Custom (Spring Boot) | **8083** | 8093 | microservices-network |
| swagger-ui | swaggerapi/swagger-ui | 8080 | - | microservices-network |
| keycloak | quay.io/keycloak/keycloak | 8080 | - | microservices-network |
| mongo-users | mongo:7 | - | microservices-network |
| mongo-passwords | mongo:7 | - | microservices-network |
| neo4j-notes | neo4j:5 | - | microservices-network |

> ⚠️ **Security Note**: Only NGINX is exposed to the host on port 80. All other services communicate internally for security.

## 🛠️ Development Commands

```bash
# Build all services
make build

# Start all services
make up

# Stop all services
make down

# Clean everything (including volumes)
make clean

# View logs
make logs

# View specific service logs
make auth-logs
make user-logs
make note-logs

# Setup Keycloak (manual steps)
make setup-keycloak
```

## 📊 Monitoring & Logs

### Log Levels
- **DEBUG**: Development debugging
- **INFO**: General information
- **WARN**: Warning messages
- **ERROR**: Error conditions

### Health Checks
Each service exposes actuator endpoints:
- `/actuator/health` - Service health status
- `/actuator/info` - Service information

## 🚨 Troubleshooting

### "Can't Connect" Despite Services Running - Diagnostics

#### Quick Diagnosis Commands
```bash
# 1. Check if containers are actually running
docker-compose ps

# 2. Check if NGINX is listening on port 80
docker-compose exec nginx netstat -tlnp | grep :80

# 3. Check NGINX configuration syntax
docker-compose exec nginx nginx -t

# 4. Test NGINX directly from inside container
docker-compose exec nginx curl -I http://localhost

# 5. Check if port 80 is exposed to host
docker port $(docker-compose ps -q nginx)

# 6. Test from host machine
curl -v http://localhost
curl -v http://127.0.0.1
curl -v http://0.0.0.0
```

#### Most Common Issues & Fixes

##### Issue 1: NGINX Container Not Properly Exposing Port
```bash
# Check Docker port mapping
docker-compose ps nginx

# Should show: 0.0.0.0:80->80/tcp
# If not, restart with explicit port binding
docker-compose down
docker-compose up nginx
```

##### Issue 2: NGINX Configuration Error
```bash
# Check NGINX error logs
docker-compose logs nginx | tail -20

# Test NGINX config
docker-compose exec nginx nginx -t

# If config error, check nginx.conf file exists
docker-compose exec nginx ls -la /etc/nginx/nginx.conf
docker-compose exec nginx cat /etc/nginx/nginx.conf
```

##### Issue 3: Services Not Ready
```bash
# Check if backend services are responding
docker-compose exec nginx curl -I http://auth-service:8080
docker-compose exec nginx curl -I http://user-service:8080
docker-compose exec nginx curl -I http://note-service:8080

# If services not ready, check their logs
docker-compose logs auth-service | tail -10
docker-compose logs user-service | tail -10
docker-compose logs note-service | tail -10
```

##### Issue 4: Host Network Issues
```bash
# Try different localhost addresses
curl http://localhost
curl http://127.0.0.1
curl http://0.0.0.0

# Check if another service is using port 80
sudo lsof -i :80
sudo netstat -tlnp | grep :80

# On macOS, check Docker Desktop network
ping host.docker.internal
```

#### Emergency Fix: Use Alternative Port
If port 80 is blocked, temporarily use port 8080:

```bash
# Stop services
docker-compose down

# Edit docker-compose.yml - change nginx ports
# From: "80:80"
# To: "8080:80"

# Restart
docker-compose up -d nginx

# Test
curl http://localhost:8080
```

#### Platform-Specific Issues

##### Windows (WSL2/Docker Desktop)
```bash
# Check Windows firewall
# Check Docker Desktop settings
# Try: http://localhost instead of http://127.0.0.1

# In PowerShell:
netstat -an | findstr :80
```

##### macOS (Docker Desktop)
```bash
# Check Docker Desktop is running
# Try restarting Docker Desktop
# Check network settings in Docker Desktop preferences

# Terminal:
lsof -i :80
netstat -an | grep :80
```

##### Linux
```bash
# Check iptables/firewall
sudo iptables -L
sudo ufw status

# Check if Docker daemon is properly configured
sudo systemctl status docker
```

### Step-by-Step Connection Test

Run these commands in order and report where it fails:

```bash
# Step 1: Basic container check
echo "=== Step 1: Container Status ==="
docker-compose ps

# Step 2: Port mapping check  
echo "=== Step 2: Port Mapping ==="
docker port $(docker-compose ps -q nginx) 2>/dev/null || echo "No nginx container found"

# Step 3: NGINX internal test
echo "=== Step 3: NGINX Internal Test ==="
docker-compose exec nginx curl -s -o /dev/null -w "%{http_code}" http://localhost || echo "NGINX internal test failed"

# Step 4: Host connection test
echo "=== Step 4: Host Connection Test ==="
curl -s -o /dev/null -w "%{http_code}" http://localhost || echo "Host connection failed"

# Step 5: Alternative port test
echo "=== Step 5: Alternative Addresses ==="
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1 || echo "127.0.0.1 failed"
curl -s -o /dev/null -w "%{http_code}" http://0.0.0.0 || echo "0.0.0.0 failed"
```

### Immediate Fix Commands

Try these in order until one works:

```bash
# Fix 1: Restart NGINX only
docker-compose restart nginx
curl http://localhost

# Fix 2: Use explicit port binding
docker-compose down
docker-compose up -d nginx
curl http://localhost

# Fix 3: Use alternative port
docker-compose down
# Edit docker-compose.yml: change "80:80" to "8080:80"
docker-compose up -d nginx
curl http://localhost:8080

# Fix 4: Complete restart
docker-compose down
docker-compose up --build
# Wait 2 minutes, then:
curl http://localhost
```
