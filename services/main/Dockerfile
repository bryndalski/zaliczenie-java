FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy everything from the build context (the main directory) to ensure pom.xml is included
COPY . /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
