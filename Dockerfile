# STAGE 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the JAR file (skipping tests to speed it up)
RUN mvn clean package -DskipTests

# STAGE 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose port 8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]