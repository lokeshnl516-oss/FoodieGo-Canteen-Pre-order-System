# Step 1: Use an official OpenJDK image to run the jar
FROM openjdk:17-jdk-slim

# Step 2: Set the working directory inside the cloud container
WORKDIR /app

# Step 3: Copy your compiled jar file into the container
COPY target/canteen-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the standard port Render expects
EXPOSE 10000

# Step 5: Command to execute your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]