FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/featureflagservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
LABEL authors="eddykaggia"

ENTRYPOINT ["java", "-jar", "app.jar"]