FROM openjdk:8-jre-alpine
MAINTAINER Alexander Ivchenko

ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD target/libs/* libs/
ENV JMX_HOST $JMX_HOST
ENV JMX_PORT $JMX_PORT
ENV SERVICE_ID $SERVICE_ID
ENV CONSUL_HOST $CONSUL_HOST
ENV CONSUL_PORT $CONSUL_PORT

ENTRYPOINT /usr/bin/java -Xmx20m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${JMX_HOST} -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -jar app.jar ${SERVICE_ID} ${CONSUL_HOST} ${CONSUL_PORT} /var/run/${SERVICE_ID}.pid
