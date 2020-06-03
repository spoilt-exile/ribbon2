FROM openjdk:11.0.7-jre
MAINTAINER Stanislav Nepochatov
COPY target/messenger-static.jar /home/messenger-static.jar
EXPOSE 9100
CMD sleep 10 && java -jar /home/messenger-static.jar
