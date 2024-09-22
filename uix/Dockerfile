FROM amazoncorretto:21
LABEL maintainer="spoilt.exile@gmail.com"
COPY target/uix-static.jar /app/uix-static.jar
ADD web /app/web
EXPOSE 9999
WORKDIR /app
CMD ["java", "-jar", "/app/uix-static.jar"]