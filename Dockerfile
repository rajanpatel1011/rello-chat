#
# Build stage
#
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Assumes `mvn package` has been run locally and `target/rello.jar` exists
COPY target/rello.jar /app/rello.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/rello.jar"]
