FROM openjdk:8-jre-alpine
MAINTAINER Alexander Ivchenko

ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD target/libs/* libs/

ENTRYPOINT /usr/bin/java -Xms512m -Xmx512m -jar app.jar /conf/conf.yaml
