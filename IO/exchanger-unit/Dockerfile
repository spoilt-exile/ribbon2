FROM openjdk:11.0.7-jre
MAINTAINER Stanislav Nepochatov
COPY target/exchanger-static.jar /home/exchanger-static.jar
EXPOSE 9102
CMD sleep 10 && java -jar /home/exchanger-static.jar
