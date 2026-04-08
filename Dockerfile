# Build stage: JAR file banayega
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Production stage: JAR run karega
FROM openjdk:17-jdk-alpine
ARG PORT=8081
ENV PORT=${PORT}
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]