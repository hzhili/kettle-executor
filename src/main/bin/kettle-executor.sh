#!/usr/bin/env bash
#项目名称
APPLICATION="kettle-executor"
#项目启动jar包名称
APPLICATION_JAR="${APPLICATION}.jar"
#进入bin目录并获取绝对路径
BIN_PATH=$(cd $(dirname $0); pwd)
cd ${BIN_PATH}
#返回上级目录
cd ..
# 打印项目根目录绝对路径
# `pwd` 执行系统命令并获得结果
ROOT=`pwd`
# 外部配置文件绝对目录,如果是目录需要/结尾，也可以直接指定文件
# 如果指定的是目录,spring则会读取目录中的所有配置文件
CONFIG_DIR=${ROOT}"/conf/"
LOG_PATH="${ROOT}/logs"
LOG_FILE="${LOG_PATH}/${APPLICATION}.log"
if [[ ! -d "${LOG_PATH}" ]]; then
 mkdir -p ${LOG_PATH}
fi
#==========================================================================================
# JVM Configuration
# -Xmx256m:设置JVM最大可用内存为256m,根据项目实际情况而定，建议最小和最大设置成一样。
# -Xms256m:设置JVM初始内存。此值可以设置与-Xmx相同,以避免每次垃圾回收完成后JVM重新分配内存
# -Xmn512m:设置年轻代大小为512m。整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。
#          持久代一般固定大小为64m,所以增大年轻代,将会减小年老代大小。此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8
# -XX:MetaspaceSize=64m:存储class的内存大小,该值越大触发Metaspace GC的时机就越晚
# -XX:MaxMetaspaceSize=320m:限制Metaspace增长的上限，防止因为某些情况导致Metaspace无限的使用本地内存，影响到其他程序
# -XX:-OmitStackTraceInFastThrow:解决重复异常不打印堆栈信息问题
#==========================================================================================
JAVA_OPT="-server -Xms512m -Xmx20148m -Xmn1024m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
LOG_OPT="--logging.config=${CONFIG_DIR}logback-spring.xml " # --logging.file.path=${LOG_PATH}
JNDI_OPT="--kettle.jdbc-file-path=${CONFIG_DIR}"
SPRING_OPT="--spring.config.location=${CONFIG_DIR} ${LOG_OPT} ${JNDI_OPT}"


is_exist(){
    #获取程序PID
    pid=$(ps -ef|grep java|grep ${APPLICATION_JAR}|awk '{print $2}')
    if [[ -z "${pid}" ]]; then #判断pid是否为空
        return 1
    else
        return 0
    fi
}
start(){
    is_exist
    if [[ $? -eq "0" ]]; then # [$? -eq "0"] 说明pid不等于空 说明服务正在运行中，将进程号打印出来
        echo "$APPLICATION is running! pid=${pid}"
    else
        nohup java ${JAVA_OPT} -jar ${ROOT}/boot/${APPLICATION_JAR}  ${SPRING_OPT} > ${LOG_FILE} 2>&1 &
        sleep 5
        echo "$APPLICATION  has started"
    fi
}
stop(){
    is_exist
    if [[ $? -eq "0" ]]; then # $? 取最后运行的命令的结束代码[返回值], 判断is_exist返回值与 0比较
        echo "$APPLICATION is stopping"
        kill -9 "$pid"
        sleep 3
        echo "$APPLICATION has stopped"
    else
        echo "$APPLICATION is not running"
    fi
}
restart(){
    is_exist
    if [[ $? -eq "0" ]]; then
        stop
        start
    else
        start
    fi
}
status(){
    is_exist
    if [[ $? -eq "0" ]]; then
        echo "${APPLICATION} is running PID:${pid}"
    else
        echo "${APPLICATION} is stopped"
    fi
}
usage(){
    echo "case: sh kettle-executor.sh [start|stop|restart|status]"
    echo "请类似这样执行 ./kettle-executor.sh start   or  ./kettle-executor.sh restart\
          or ./kettle-executor.sh stop or ./kettle-executor.sh status"
    exit 1
}

case "$1" in
    "start")
        start
        ;;
    "stop")
        stop
        ;;
    "restart")
        restart
        ;;
    "status")
        status
        ;;
    *)
        usage
        ;;
esac