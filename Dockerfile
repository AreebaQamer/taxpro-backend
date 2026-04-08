
# Build stage
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - YEH CHANGE KIYA HAI
FROM eclipse-temurin:17-jdk-alpine
ARG PORT=8081
ENV PORT=${PORT}
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
