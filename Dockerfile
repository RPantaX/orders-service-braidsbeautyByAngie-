FROM openjdk:17-jdk-slim
WORKDIR /app
COPY application/target/application-0.0.1-SNAPSHOT.jar orders-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "orders-service.jar"]