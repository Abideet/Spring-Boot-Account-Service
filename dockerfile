FROM openjdk:21-jdk-slim


WORKDIR /app


COPY build/libs/AccountService-0.0.1-SNAPSHOT.jar /app/account-service.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "/app/account-service.jar"]