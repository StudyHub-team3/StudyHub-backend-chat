FROM amazoncorretto:17
VOLUME /tmp
EXPOSE 8080
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/ ./urandom", "-jar", "/app.jar"]