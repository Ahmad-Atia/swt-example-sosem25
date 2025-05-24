FROM maven:3.9.9-eclipse-temurin-24-alpine
# Set the working directory
WORKDIR /app
COPY target/proj3-0.0.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]