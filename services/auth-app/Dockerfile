# Use Maven with Eclipse Temurin JDK 21 as the build image
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project directory to the container
COPY . .

# Package the application, skipping tests
RUN mvn clean package -DskipTests

# Use Eclipse Temurin JRE 21 as the base image for the final image
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar from the target directory to the container
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
