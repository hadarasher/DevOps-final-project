FROM openjdk:21-jdk
LABEL authors="hadar"

VOLUME /tmp

WORKDIR /app

#COPY target/DevOps-final-project.jar /app/final-project.jar
COPY target/final-project-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
