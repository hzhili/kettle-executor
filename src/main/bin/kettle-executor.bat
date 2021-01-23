@echo off
setlocal
cd /D %~dp0
:: *************
:: 应用名称
:: *************
set APPLICATION=kettle-executor
set APPLICATION_JAR=%APPLICATION%.jar
:: *************
:: 脚本文件路径
:: *************
set BIN_PATH=%~dp0
:: echo "脚本目录:%BIN_PATH%"
cd %BIN_PATH%
cd ..
:: *************************
:: 应用程序主目录
:: *************************
set APPLICATION_HOME=%cd%
:: echo "应用目录:%APPLICATION_HOME%"
goto 1
外部配置文件绝对目录,如果是目录需要/结尾,也可以直接指定文件
如果指定的是目录,spring则会读取目录中的所有配置文件

:1
set CONFIG_DIR=%APPLICATION_HOME%\conf\
:: echo "配置文件目录:%CONFIG_DIR%"
set LOG_PATH=%APPLICATION_HOME%\logs
:: echo "日志文件路径:%LOG_PATH%
set LOG_FILE=%LOG_PATH%\%APPLICATION%.log
:: echo "日志文件全路径:%LOG_FILE%"
goto 2
REM JVM Configuration
REM -Xmx256m:设置JVM最大可用内存为256m,根据项目实际情况而定，建议最小和最大设置成一样。
REM -Xms256m:设置JVM初始内存。此值可以设置与-Xmx相同,以避免每次垃圾回收完成后JVM重新分配内存
REM -Xmn512m:设置年轻代大小为512m。整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。
REM         持久代一般固定大小为64m,所以增大年轻代,将会减小年老代大小。此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8
REM -XX:MetaspaceSize=64m:存储class的内存大小,该值越大触发Metaspace GC的时机就越晚
REM -XX:MaxMetaspaceSize=320m:限制Metaspace增长的上限，防止因为某些情况导致Metaspace无限的使用本地内存，影响到其他程序
REM -XX:-OmitStackTraceInFastThrow:解决重复异常不打印堆栈信息问题
:2
set JAVA_OPT=-server -Xms512m -Xmx2048m -Xmn1024m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m
set JAVA_OPT=%JAVA_OPT% -XX:-OmitStackTraceInFastThrow
set LOG_OPT=--logging.config=%CONFIG_DIR%logback-spring.xml --logging.file.path=%LOG_PATH%
set JNDI_OPT=--kettle.jdbc-file-path=%CONFIG_DIR%
set SPRING_OPT=--spring.config.location=%CONFIG_DIR% %LOG_OPT% %JNDI_OPT%

:: echo "SPRING 参数:%SPRING_OPT%"
:: echo "JAVA 参数: %JAVA_OPT%"


java %JAVA_OPT% -jar %APPLICATION_HOME%\boot\%APPLICATION_JAR%  %SPRING_OPT%