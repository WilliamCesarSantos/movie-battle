#https://docs.docker.com/language/java/build-images/
FROM openjdk:16-alpine3.13

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src/main ./src/main

CMD ["./mvnw", "spring-boot:run"]