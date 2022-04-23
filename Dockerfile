FROM openjdk:8-jdk-alpine

MAINTAINER Dabisa Kelava

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} ./app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
