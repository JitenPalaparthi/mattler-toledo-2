FROM maven:3.9.9-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml ..
COPY ../pom.xml /app/..
COPY . .
#RUN mvn -q -pl api-gateway -am -DskipTests package
RUN mvn -q -pl api-gateway -am -DskipTests package spring-boot:repackage
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
