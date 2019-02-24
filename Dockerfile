FROM JAVA:8
EXPOSE 8080
ADD target/Rello.jar Rello.jar
ENTRYPOINT ["java","-jar","Rello.jar"]