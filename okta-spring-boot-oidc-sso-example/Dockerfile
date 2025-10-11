# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-11 AS build

# Create a non-root user and group
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
# *** THIS IS THE FIX: Grant ownership of the home directory to the new user ***
RUN chown -R appuser:appgroup /home/appuser

# Create app directory
WORKDIR /app

# Switch to the non-root user
USER appuser

# Copy the pom.xml and the source code (now as appuser)
COPY pom.xml .
COPY src ./src

# Build the application, skipping tests for faster builds
RUN mvn clean install -DskipTests

# Stage 2: Create the final, smaller image with only the JRE
FROM openjdk:11-jre-slim

# Create the same non-root user and group
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy the built JAR file from the 'build' stage and set ownership
COPY --from=build --chown=appuser:appgroup /app/target/spring-boot-oidc-sso-0.0.1-SNAPSHOT.jar app.jar

# Switch to the non-root user for running the application
USER appuser

# Expose the port the app will run on (Render will set this via PORT env var)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]