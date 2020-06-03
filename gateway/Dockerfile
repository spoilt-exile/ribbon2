FROM openjdk:11.0.7-jre
MAINTAINER Stanislav Nepochatov
COPY target/gateway-static.jar /home/gateway-static.jar
EXPOSE 9000
CMD sleep 10 && java -jar /home/gateway-static.jar
