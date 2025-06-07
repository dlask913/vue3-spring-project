FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/noticeboard-service-0.0.1-SNAPSHOT.jar spring-app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
CMD ["java", "-jar", "/app/spring-app.jar"]
