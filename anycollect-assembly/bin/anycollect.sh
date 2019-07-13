#!/bin/bash

ACTION=status
ENABLE_JFR=false
ENABLE_DEBUG=false
DEBUG_PORT=5005
while (( "$#" )); do
    case "$1" in
        -a|--additional-anycollect-args)
            ADDITIONAL_ANYCOLLECT_ARGS=$2
            shift 2
            ;;
        --enable-jfr)
            ENABLE_JFR=$2
            shift 2
            ;;
        --debug)
            ENABLE_DEBUG=true
            DEBUG_PORT=$2
            shift 2
            ;;
        --start)
            ACTION=start
            shift
            ;;
        --status)
            ACTION=status
            shift
            ;;
        --stop)
            ACTION=stop
            shift
            ;;
        --restart)
            ACTION=restart
            shift
            ;;
        --run)
            ACTION=run
            shift
            ;;
        --) # end argument parsing
            shift
            break
            ;;
        -*|--*=) # unsupported flags
            echo "Error: Unsupported flag $1" >&2
            exit 1
            ;;
    esac
done

# Java
JAVA_HOME=${JAVA_HOME:-"/usr"}
ANYCOLLECT_JAR=${ANYCOLLECT_JAR:-"./lib/anycollect.jar"}
JAVA_OPTS=${JAVA_OPTS:-""}
JAVA=${JAVA:-"${JAVA_HOME}/bin/java"}

# Java Flight Recorder
if [[ ${ENABLE_JFR} = true ]]; then
    JFR_OPTS="-XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
    JAVA_OPTS="${JAVA_OPTS} ${JFR_OPTS}"
fi

# Debug
if [[ ${ENABLE_DEBUG} = true ]]; then
    DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${DEBUG_PORT}"
    JAVA_OPTS="${JAVA_OPTS} ${DEBUG_OPTS}"
fi

# Class-Path
ANYCOLLECT_EXT=${ANYCOLLECT_EXT:-"./extensions/*"}
TOOLS_JAR="${JAVA_HOME}/lib/tools.jar"
CLASSPATH=${CLASSPATH:-"${TOOLS_JAR}:${ANYCOLLECT_EXT}:${ANYCOLLECT_JAR}"}

# pid
PID_FILE=${PID_FILE:-"./var/run/anycollect.pid"}

# AnyCollect
LOG_DIR=${LOG_DIR:-"./logs"}
LOG_LEVEL=${LOG_LEVEL:-"debug"}
ANYCOLLECT_OPTS=${ANYCOLLECT_OPTS:-"-Danycollect.log.dir=${LOG_DIR} -Danycollect.log.level=${LOG_LEVEL}"}
ANYCOLLECT_CONF_FILE=${ANYCOLLECT_CONF_FILE:-"./etc/anycollect.yaml"}
LOGBACK_CONF_FILE=${LOGBACK_CONF_FILE:-"./etc/logback.xml"}
ADDITIONAL_ANYCOLLECT_ARGS=${ADDITIONAL_ANYCOLLECT_ARGS:-""}
ANYCOLLECT_ARGS=${ANYCOLLECT_ARGS:-"--logback-conf=${LOGBACK_CONF_FILE} --pid-file=${PID_FILE} ${ADDITIONAL_ANYCOLLECT_ARGS}"}
MAIN_CLASS="io.github.anycollect.Init"

getAnyCollectPid() {
    JPS=${JPS:-"${JAVA_HOME}/bin/jps -l"}
    PID_EXEC="$JPS | grep anycollect | awk '{print \$1};'"
    echo $(eval ${PID_EXEC})
}

# JMX
JMX_PORT=${JMX_PORT:-"9797"}
MONITOR_OPTS=${MONITOR_OPTS:-"-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=${JMX_PORT}"}

# GC
HEAP_INIT=${HEAP_INIT:-"256m"}
HEAP_MAX=${HEAP_MAX:-"512m"}
GC_OPTS=${GC_OPTS:-"-Xms${HEAP_INIT} -Xmx${HEAP_MAX}"}

createExecLine() {
    echo "${JAVA} -server ${JAVA_OPTS} ${GC_OPTS} ${MONITOR_OPTS} ${ANYCOLLECT_OPTS} -cp ${CLASSPATH} ${MAIN_CLASS} ${ANYCOLLECT_ARGS}"
}

assertAnyCollectIsNotRunning() {
    pid=$(getAnyCollectPid)
    if [[ ! -z ${pid} ]]; then
        echo "AnyCollect is running, pid ${pid}"
        exit 1
    fi
}

start() {
    assertAnyCollectIsNotRunning
    nohup sh -c "$(createExecLine) &"
}

run() {
    assertAnyCollectIsNotRunning
    sh -c "$(createExecLine)"
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
                echo "Waiting for graceful shutdown, pid=${pid}"
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

case ${ACTION} in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        restart
    ;;
    run)
        run
    ;;
    status)
        status
    ;;
    *)
        echo $"Usage: $0 {start|stop|restart|run|status}"
    ;;
esac

exit 0