# Stage 1: Build the Maven application inside Render's environment
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy your source code and configurations
COPY pom.xml .
COPY src ./src

# Compile and package the application into a JAR file, skipping tests for speed
RUN mvn clean package -DskipTests

# Stage 2: Run the compiled application using a slim runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the compiled JAR file directly from the build stage above
COPY --from=build /app/target/canteen-0.0.1-SNAPSHOT.jar app.jar

# Expose port and run the server
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]