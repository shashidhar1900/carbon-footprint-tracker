# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/target/analytics-servcie-0.0.1-SNAPSHOT.jar app.jar

# Expose port (change if necessary)
EXPOSE 8083

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/carbon_tracker?createDatabaseIfNotExist=true
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=Shashi@123
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://host.docker.internal:8761/eureka/

# Run the application
CMD ["java", "-jar", "app.jar"]

