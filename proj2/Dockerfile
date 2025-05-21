FROM maven:3.9.9-eclipse-temurin-24-alpine
# Set the working directory
WORKDIR /app

COPY target/*-shaded.jar ./target/app-shaded.jar
# Run the application
CMD ["java", "-jar", "target/app-shaded.jar"]