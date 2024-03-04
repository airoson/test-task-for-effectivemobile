FROM maven:3.9.4-eclipse-temurin-17 as build

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package -DskipTests=true

FROM bellsoft/liberica-openjdk-debian:17

WORKDIR /app

COPY --from=build target/test-task-for-effectivemobile-0.0.1-SNAPSHOT.jar ./application.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./application.jar"]
