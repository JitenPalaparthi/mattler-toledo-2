# Build
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Run
FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /workspace/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
