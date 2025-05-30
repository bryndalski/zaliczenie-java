# Auth Application

This project is a user authentication system built with Spring Boot, utilizing JWT (JSON Web Tokens) for secure authentication and MongoDB for data storage.

## Features

- User registration
- User login
- JWT-based authentication
- Secure endpoints
- MongoDB integration

## Technologies Used

- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- MongoDB
- Maven

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

## Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd auth-app
   ```

2. **Configure MongoDB:**
   Ensure you have MongoDB installed and running. Update the `application.properties` file with your MongoDB connection details.

3. **Build the project:**
   Use Maven to build the project:
   ```
   mvn clean install
   ```

4. **Run the application:**
   Start the Spring Boot application:
   ```
   mvn spring-boot:run
   ```

5. **Access the API:**
   The application will be running on `http://localhost:8080`. You can use tools like Postman to test the authentication endpoints.

## API Endpoints

- **POST /api/auth/register**: Register a new user.
- **POST /api/auth/login**: Authenticate a user and return a JWT.

## License

This project is licensed under the MIT License. See the LICENSE file for details.