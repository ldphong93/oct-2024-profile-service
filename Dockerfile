FROM openjdk:17-jdk
WORKDIR /app
ADD ./target/profile-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
