#!/bin/bash

# Java
JAVA_HOME=${JAVA_HOME:-"/usr"}
ANYCOLLECT_JAR=${ANYCOLLECT_JAR:-"./lib/anycollect.jar"}
JAVA_OPTS=${JAVA_OPTS:-""}
ANYCOLLECT_EXT=${ANYCOLLECT_EXT:-"./extensions/*"}
CLASSPATH=${CLASSPATH:-"${ANYCOLLECT_EXT}"}
JAVA=${JAVA:-"${JAVA_HOME}/bin/java"}

# pid
JPS=${JPS:-"${JAVA_HOME}/bin/jps -l"}
PID_FILE=${PID_FILE:-"./var/run/anycollect.pid"}
PID_EXEC="$JPS | grep anycollect | awk '{print \$1};'"

LOG_DIR=${LOG_DIR:-"./logs"}
LOG_LEVEL=${LOG_LEVEL:-"debug"}
ANYCOLLECT_OPTS=${ANYCOLLECT_OPTS:-"-Danycollect.log.dir=${LOG_DIR} -Danycollect.log.level=${LOG_LEVEL}"}
ANYCOLLECT_CONF_FILE=${ANYCOLLECT_CONF_FILE:-"./etc/anycollect.yaml"}
LOGBACK_CONF_FILE=${LOGBACK_CONF_FILE:-"./etc/logback.xml"}
ANYCOLLECT_ARGS=${ANYCOLLECT_ARGS:-"--conf=${ANYCOLLECT_CONF_FILE} --logback-conf=${LOGBACK_CONF_FILE} --pid-file=${PID_FILE}"}

getAnyCollectPid() {
    echo $(eval ${PID_EXEC})
}

# JMX
JMX_PORT=${JMX_PORT:-"9797"}
MONITOR_OPTS=${MONITOR_OPTS:-"-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=${JMX_PORT}"}

# GC
HEAP_INIT=${HEAP_INIT:-"256m"}
HEAP_MAX=${HEAP_MAX:-"512m"}
GC_OPTS=${GC_OPTS:-"-Xms${HEAP_INIT} -Xmx${HEAP_MAX}"}


start() {
    pid=$(getAnyCollectPid)
    if [[ ! -z ${pid} ]]; then
        echo "AnyCollect is running, pid ${pid}"
        exit 1
    fi
    nohup ${JAVA} -server ${JAVA_OPTS} ${GC_OPTS} ${MONITOR_OPTS} ${ANYCOLLECT_OPTS} -cp ${CLASSPATH} -jar ${ANYCOLLECT_JAR} ${ANYCOLLECT_ARGS} &
}

status() {
    pid=$(getAnyCollectPid)
    if [[ ! -z ${pid} ]]; then
        echo "UP $pid"
    else
        echo "DOWN"
    fi
}

stop() {
    pid=$(getAnyCollectPid)
    if [[ ! -z ${pid} ]]; then
        kill -15 ${pid}
        while (true); do
            ps -p ${pid} >/dev/null
            if [[ $? -eq 0 ]]; then
                echo "Waiting for graceful shutdown"
            else
                echo "AnyCollect has been stopped gracefully"
                break
            fi
            sleep 1
        done
    else
        echo "AnyCollect was not running"
    fi
}

restart() {
    stop
    start
}

case $1 in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        restart
    ;;
    status)
        status
    ;;
    *)
        echo $"Usage: $0 {start|stop|restart|status}"
    ;;
esac

exit 0