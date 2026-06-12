FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package spring-boot:repackage -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN useradd -m appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]