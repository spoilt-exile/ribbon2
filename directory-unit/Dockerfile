FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/directory-static.jar /app/directory-static.jar
EXPOSE 9101
WORKDIR /app
CMD ["java", "-jar", "/app/directory-static.jar"]
