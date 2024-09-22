FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/exchanger-static.jar /app/exchanger-static.jar
EXPOSE 9102
WORKDIR /app
CMD ["java", "-jar", "/app/exchanger-static.jar"]
