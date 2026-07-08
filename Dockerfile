# పాతది తీసేసి ఇది పెట్టు
FROM eclipse-temurin:11-jre-slim
COPY target/SmartStore-Backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]