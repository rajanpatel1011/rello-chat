#
# Build stage
#
FROM maven:3.8.7-eclipse-temurin-17-focal AS build
WORKDIR /home/app
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn clean install


FROM eclipse-temurin:17-jre
COPY --from=build /home/app/target/rello.jar /usr/local/lib/rello.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/rello.jar"]
