#!/usr/bin/env bash
#最新部署文件上传目录
TAR_PATH="/opt"
#部署目录
BS_PATH="/usr/local"
#项目名称
APPLICATION="kettle-executor"
#项目启动jar包名称
APPLICATION_JAR="${APPLICATION}.jar"
#部署完成后应用目录
APPLICATION_HOME="/usr/local/kettle-executor"
is_exist(){
    #获取程序PID
    pid=$(ps -ef|grep java|grep ${APPLICATION_JAR}|awk '{print $2}')
    if [[ -z "${pid}" ]]; then #判断pid是否为空
        return 1
    else
        return 0
    fi
}
bs(){
    tar -zxvf ${TAR_PATH}/kettle-executor.tar.gz -C ${BS_PATH}
    cd ${APPLICATION_HOME}/bin
    exec ./kettle-executor.sh start
}

#判断程序是否运行,如果运行就先停止再重新部署
is_exist
if [[ $? -eq "0" ]]; then # $? 取最后运行的命令的结束代码[返回值], 判断is_exist返回值与 0比较
    cd ${APPLICATION_HOME}/bin
    exec ./kettle-executor.sh stop
    rm -rf ${APPLICATION_HOME}
    bs
else
    rm -rf ${APPLICATION_HOME}
    bs
fi
