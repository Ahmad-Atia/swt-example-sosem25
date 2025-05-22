FROM maven:3.9.9-eclipse-temurin-24-alpine
# Set the working directory
WORKDIR /app
COPY target/*-shaded.jar app-shaded.jar
CMD ["java", "-jar", "app-shaded.jar"]