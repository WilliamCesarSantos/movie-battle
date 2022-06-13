#https://docs.docker.com/language/java/build-images/
#https://mmarcosab.medium.com/criando-container-docker-com-aplica%C3%A7%C3%B5es-java-bfc8e6329c4e
FROM openjdk:11

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src/main ./src/main

CMD ["./mvnw", "spring-boot:run"]