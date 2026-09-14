# ---- Etapa de build ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Cachear dependencias antes de copiar el código fuente
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

# ---- Etapa de ejecución ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:${PORT:-${SERVER_PORT:-8080}}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
