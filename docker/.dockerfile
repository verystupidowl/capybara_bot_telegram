FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package
EXPOSE 8080
CMD ["java", "-jar", "target/capybara-telegram-bot-0.0.1-SNAPSHOT.jar"]