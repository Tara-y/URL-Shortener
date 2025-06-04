FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/urlShortener*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]