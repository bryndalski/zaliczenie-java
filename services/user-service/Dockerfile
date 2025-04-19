# Use an official OpenJDK runtime as a parent image for building
FROM openjdk:17-jdk-slim AS build

# Install dependencies (curl, unzip) and Gradle
RUN apt-get update && apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Ensure Gradle wrapper is executable
RUN chmod +x gradlew

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew clean build --no-daemon

# Debug: List the contents of build/libs
RUN ls -l build/libs

# Runtime image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/*.jar auth-service.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "user-service.jar"]
