FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/server-static.jar /app/server-static.jar
EXPOSE 9000
WORKDIR /app
CMD ["java", "-jar", "/app/server-static.jar"]