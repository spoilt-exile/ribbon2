FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/gateway-static.jar /app/gateway-static.jar
EXPOSE 9000
WORKDIR /app
CMD ["java", "-jar", "/app/gateway-static.jar"]
