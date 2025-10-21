FROM maven:3.9.9-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml ..
COPY ../pom.xml /app/..
COPY . .
#RUN mvn -q -pl api-gateway -am -DskipTests package
RUN mvn -q -pl inventory-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=build /app/inventory-service/target/inventory-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
