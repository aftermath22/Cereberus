# --- Build Stage ---
# Use an official Maven image to build the application's JAR file.
FROM maven:3.8.5-openjdk-11 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker's layer caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Build the application, skipping tests for a faster build
RUN ./mvnw clean install -DskipTests

# --- Run Stage ---
# Use a slim Java runtime image for a smaller final image size
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port the application will run on (optional but good practice)
EXPOSE 8080

# The command to run your application when the container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]