FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/note-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
