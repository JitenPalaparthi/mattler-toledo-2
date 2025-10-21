# discovery-server/Dockerfile
FROM maven:3.9.9-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml ..
COPY ../pom.xml /app/..o
COPY . .
RUN mvn -q -pl discovery-server -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=build /app/discovery-server/target/discovery-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
