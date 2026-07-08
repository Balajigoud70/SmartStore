FROM eclipse-temurin:11-jdk-alpine
COPY target/SmartStore-Backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]