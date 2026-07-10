# Stage 1: Build the Maven application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application with installed SSL certificates
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Install native SSL certificates inside the container so it can trust cloud DBs
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*

# Copy the compiled JAR file from the build stage
COPY --from=build /app/target/canteen-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]