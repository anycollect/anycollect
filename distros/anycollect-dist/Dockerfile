FROM openjdk:8-jre-alpine
MAINTAINER Alexander Ivchenko

ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD target/libs/* libs/

ENTRYPOINT /usr/bin/java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${JMX_HOST} -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Xms512m -Xmx512m -jar app.jar /conf/conf.yaml
