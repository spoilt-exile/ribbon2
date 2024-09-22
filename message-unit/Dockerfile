FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/messenger-static.jar /app/messenger-static.jar
EXPOSE 9100
WORKDIR /app
CMD ["java", "-jar", "/app/messenger-static.jar"]
