# Authentication System with JWT and MongoDB

This project implements a user authentication system using JSON Web Tokens (JWT) and MongoDB. It is built with Spring Boot and provides a RESTful API for user registration and login.

## Features

- User registration
- User login
- JWT-based authentication
- Secure endpoints
- MongoDB integration

## Project Structure

```
auth-app
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── auth
│   │   │               ├── AuthApplication.java
│   │   │               ├── config
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller
│   │   │               │   └── AuthController.java
│   │   │               ├── model
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   └── UserRepository.java
│   │   │               ├── security
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── JwtTokenProvider.java
│   │   │               │   └── CustomUserDetailsService.java
│   │   │               └── service
│   │   │                   └── AuthService.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── README.md
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MongoDB

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd auth-app
   ```
3. Update the `application.properties` file with your MongoDB connection details.

### Running the Application

To run the application, use the following command:
```
mvn spring-boot:run
```

### API Endpoints

- **POST /api/auth/register**: Register a new user.
- **POST /api/auth/login**: Authenticate a user and return a JWT.

## License

This project is licensed under the MIT License. See the LICENSE file for details.