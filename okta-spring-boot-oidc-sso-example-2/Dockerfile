# Use a multi-stage build to create a small, secure production image.
# See https://docs.docker.com/develop/develop-images/multistage-build/
FROM maven:3.6.3-jdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and maven wrapper files
COPY pom.xml .
COPY .mvn .mvn

# Download dependencies
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src src

# Build the application
RUN mvn package -DskipTests

# The second stage of the build
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]