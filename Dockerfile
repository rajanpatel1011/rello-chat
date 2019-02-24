FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD target/rello.jar rello.jar
ENTRYPOINT ["java","-jar","rello.jar"]
