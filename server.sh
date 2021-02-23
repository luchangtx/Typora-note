#!/bin/sh

###
SERVICE_NAME="$2"

#ENVFILE="env"

PIDFILE=temp/"$2"_"pid"

checkRunning(){
    if [ -f "$PIDFILE" ]; then
       if  [ -z "`cat $PIDFILE`" ];then
        echo "ERROR: Pidfile '$PIDFILE' exists but contains no pid"
        return 2
       fi
       PID="`cat ${PIDFILE}`"
       RET="`ps -p "${PID}"|grep java`"
       if [ -n "$RET" ];then
         return 0;
       else
         return 1;
       fi
    else
         return 1;
    fi
}

status(){
    if ( checkRunning );then
         PID="`cat $PIDFILE`"
         echo "'$SERVICE_NAME' is running (pid '$PID')"
         exit 0
    fi
    echo "'$SERVICE_NAME' not running"
    exit 1
}


#start
start(){
    if ( checkRunning );then
      PID="`cat $PIDFILE`"
      echo "INFO: Process with pid '$PID' is already running"
      exit 0
    fi
    #ENVIRONMENT="`cat ${ENVFILE}`"
    #java -jar -Xms64M -Xmx128M ${SERVICE_NAME} --spring.profiles.active=${ENVIRONMENT} >start-log/${SERVICE_NAME}-console.log 2>&1 &
    java -jar -Xms64m -Xmx128m ${SERVICE_NAME} -XX:PermSize=64M -XX:MaxPermSize=128M >logs/start-log/${SERVICE_NAME}-console.log 2>&1 &
    echo $! > "${PIDFILE}"; # $! > filePath 打印进程号到文件
}
#stop
stop(){
    if ( checkRunning ); then
       PID="`cat ${PIDFILE}`"
       echo "INFO: sending SIGKILL to pid '$PID'"
       kill -KILL $PID
       RET="$?" #获取上一个命令的退出状态
       rm -f "${PIDFILE}"
       return $RET
    fi
    echo "INFO: not running, nothing to do"
    return 0
}

show_help() {
    cat << EOF
Tasks provided by the sysv init script:
    stop            - terminate instance in a drastic way by sending SIGKILL
    start           - start new instance
    restart         - stop running instance (if there is one), start new instance
    status          - check if '$SERVICE_NAME' process is running
EOF
  exit 1
}

# show help
if [ -z "$1" ];then
 show_help
fi

case "$1" in
  status)
    status
    ;;
  restart)
    if ( checkRunning );then
      $0 stop ${SERVICE_NAME}
      echo
    fi
    $0 start ${SERVICE_NAME}
    $0 status ${SERVICE_NAME}
    ;;
  start)
    start
    ;;
  stop)
    stop
    exit $?
    ;;
  *)
esac
