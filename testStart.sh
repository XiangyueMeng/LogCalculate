#!/bin/bash
APP=$1
APP=${APP:-"FullNode"}
START_OPT=`echo ${@:2}`
JAR_NAME="$APP.jar"
MAX_STOP_TIME=60

checkpid() {
 pid=`ps -ef | grep $JAR_NAME |grep -v grep | awk '{print $2}'`
 return $pid
}

stopService() {
  count=1
  while [ $count -le $MAX_STOP_TIME ]; do
    checkpid
    if [ $pid ]; then
       kill -15 $pid
       sleep 1
    else
       echo "java-tron stop"
       return
    fi
    count=$[$count+1]
    if [ $count -eq $MAX_STOP_TIME ]; then
      kill -9 $pid
      sleep 1
    fi
  done
}

startService() {
 echo `date` >> start.log
 total=`cat /proc/meminfo  |grep MemTotal |awk -F ' ' '{print $2}'`
 xmx=`echo "$total/1024/1024*0.8" | bc |awk -F. '{print $1"g"}'`
 xmn=`echo "$total/1024*0.8*0.17" | bc |awk -F. '{print $1"m"}'`
 logtime=`date +%Y-%m-%d_%H-%M-%S`
 nohup java -Xmx$xmx -Xms$xmx -Xmn$xmn -XX:PermSize=256m -XX:+UseConcMarkSweepGC -Xss256k -XX:SurvivorRatio=6 -XX:MaxTenuringThreshold=2\
 -Xss256k -XX:MaxDirectMemorySize=200m -XX:+CMSParallelRemarkEnabled -XX:+ExplicitGCInvokesConcurrent\
 -XX:CMSInitiatingOccupancyFraction=90 -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSFullGCsBeforeCompaction=1 -XX:+CMSClassUnloadingEnabled\
 -XX:+TieredCompilation -XX:CICompilerCount=4 -XX:ReservedCodeCacheSize=256m -XX:+CMSScavengeBeforeRemark\
 -XX:+PrintGCDetails -Xloggc:./gc.log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution\
 -jar $JAR_NAME $START_OPT -c config.conf  >> start.log 2>&1 &

 pid=`ps -ef |grep $JAR_NAME |grep -v grep |awk '{print $2}'`
 echo "start java-tron with pid $pid on $HOSTNAME"
}

stopService
startService
