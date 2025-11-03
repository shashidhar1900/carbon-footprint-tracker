# Builder stage: build the application with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only Maven files first for better caching
COPY pom.xml .
# If you have a settings.xml or wrapper, copy them as needed
RUN mvn -B -f pom.xml dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage: use a small JRE image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Optional: debug to confirm jar exists
RUN ls -l /app

# Expose the service port used by the application
EXPOSE 8084

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
