# --- Build Stage ---
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Add executable permissions to the Maven wrapper
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Build the application
RUN ./mvnw clean install -DskipTests

# --- Run Stage ---
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]