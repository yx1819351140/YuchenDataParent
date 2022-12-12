#!/bin/bash

# shellcheck disable=SC2046
# shellcheck disable=SC2164
# shellcheck disable=SC2231
####################################################
##    作者:肖振男
##    功能:Yuchen程序环境相关设置
 ##   备注: 规定了yuchen程序的相关服务器环境
####################################################
source ${HOME}/.bash_profile
source /etc/profile
JAVA_HOME=${JAVA_HOME}

OS="Unknown"
CPSP=":"
SEPARATOR="/"
if [ "$(uname)" == "Darwin" ]; then
  OS="Mac OS X"
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
  OS="Linux"
elif [ "$(expr substr $(uname -s) 9 2)" == "NT" ]; then
  CPSP=";"
  SEPARATOR="\\"
  OS="Windows"
fi

#echo "当前运行环境: "$OS

YUCHEN_HOME=${YUCHEN_HOME}
if [ -z "${YUCHEN_HOME}" ]; then
  YUCHEN_HOME="$(

    cd "$(dirname "$0")"${SEPARATOR}..
    pwd
  )"
fi

#声明变量
YUCHEN_HOME=${YUCHEN_HOME}
YUCHEN_LIB_DIR=$YUCHEN_HOME${SEPARATOR}lib
YUCHEN_BIN_DIR=$YUCHEN_HOME${SEPARATOR}bin
YUCHEN_CONF_DIR=$YUCHEN_HOME${SEPARATOR}conf
YUCHEN_LOG_DIR=$YUCHEN_HOME${SEPARATOR}logs
YUCHEN_TMP_DIR=$YUCHEN_HOME${SEPARATOR}tmp
YUCHEN_PROGRAM_DIR=$YUCHEN_HOME${SEPARATOR}program

#创建临时文件夹文件夹
if [ -z "$YUCHEN_HOME" ]; then
  mkdir -p $YUCHEN_LOG_DIR
  mkdir -p $YUCHEN_TMP_DIR
fi

# Find the java binary
if [ -n "${JAVA_HOME}" ]; then
  JAVA_RUN="${JAVA_HOME}${SEPARATOR}bin${SEPARATOR}java"
else
  if [ $(command -v java) ]; then
    JAVA_RUN="java"
  else
    echo "JAVA_HOME is not set" >&2
    exit 1
  fi
fi

#java class path
CLASS_PATH=".$CPSP$JAVA_HOME${SEPARATOR}lib$CPSP$JAVA_HOME${SEPARATOR}lib${SEPARATOR}dt.jar$CPSP$JAVA_HOME${SEPARATOR}lib${SEPARATOR}tools.jar"
# 这里目前没有需要额外加载的依赖,所以暂时不需要加载额外的依赖
#for jar in $(find $YUCHEN_LIB_DIR -name *.jar); do
#  CLASS_PATH="$CLASS_PATH$CPSP$jar"
#done

#echo "$JAVA_HOME"
#echo "$JAVA_RUN"
#声明变量
export SEPARATOR=$SEPARATOR
export CPSP=$CPSP
export OS=$OS
export YUCHEN_HOME=${YUCHEN_HOME}
export YUCHEN_LIB_DIR=${YUCHEN_LIB_DIR}
export YUCHEN_CONF_DIR=${YUCHEN_CONF_DIR}
export YUCHEN_PROGRAM_DIR=${YUCHEN_PROGRAM_DIR}
export YUCHEN_BIN_DIR=${YUCHEN_BIN_DIR}
export YUCHEN_LOG_DIR=${YUCHEN_LOG_DIR}
export YUCHEN_TMP_DIR=${YUCHEN_TMP_DIR}
export YUCHEN_CLASS_PATH=$CLASS_PATH
export YUCHEN_JAVA_RUN=${JAVA_RUN}

export YUCHEN_CURRENT_USER=${USER}
export YUCHEN_CUSTOM_OPTS="-Dyuchen.base.dir=${YUCHEN_HOME}"
export YUCHEN_MONITOR_JAVA_OPTS=${YUCHEN_CUSTOM_OPTS}" -Xms512m -Xmx1024m -Djava.awt.headless=true -Dlogging.config=${YUCHEN_CONF_DIR}${SEPARATOR}logback-monitor.xml -Dlog.path=${YUCHEN_LOG_DIR} -Dspring.config.location=${YUCHEN_CONF_DIR}/application-monitor-dev.yml"
export YUCHEN_DATA_SERVICE_JAVA_OPTS=${YUCHEN_CUSTOM_OPTS}" -Xms2048m -Xmx4096m -Djava.awt.headless=true -Dlogging.config=${YUCHEN_CONF_DIR}${SEPARATOR}logback-data-service.xml -Dlog.path=${YUCHEN_LOG_DIR} -Dspring.config.location=${YUCHEN_CONF_DIR}/application-data-service-dev.yml"
#printf "===========================debug start========================="
#echo "SEPARATOR:"$SEPARATOR
#echo "CPSP:"$CPSP
#echo "OS:"$OS
#echo "YUCHEN_HOME:"$YUCHEN_HOME
#echo "YUCHEN_LIB_DIR:"$YUCHEN_LIB_DIR
#echo "YUCHEN_PROGRAM_DIR:"$YUCHEN_PROGRAM_DIR
#echo "YUCHEN_BIN_DIR:"$YUCHEN_BIN_DIR
#echo "YUCHEN_LOG_DIR:"$YUCHEN_LOG_DIR
#echo "YUCHEN_CONF_DIR:"$YUCHEN_CONF_DIR
#echo "YUCHEN_TMP_DIR:"$YUCHEN_TMP_DIR
#echo "YUCHEN_CLASS_PATH:"$YUCHEN_CLASS_PATH
#echo "YUCHEN_JAVA_RUN:"$YUCHEN_JAVA_RUN
#echo "YUCHEN_CURRENT_USER:"$YUCHEN_CURRENT_USER
#echo "YUCHEN_MONITOR_JAVA_OPTS:"$YUCHEN_MONITOR_JAVA_OPTS
#echo "YUCHEN_CUSTOM_OPTS:"$YUCHEN_CUSTOM_OPTS
#printf "===========================debug end========================="
#Info日志输出
function logger_info (){
	echo -e `date +%Y-%m-%d\ %H:%M:%S` : "INFO [${0##*/}] : ${1}"
	return $?

}

#错误日志输出
function logger_err (){
	echo -e `date +%Y-%m-%d\ %H:%M:%S` : "ERROR [${0##*/}] : ${1}"
	return $?
}

#加载properties配置文件
function load_conf() {
    value=`cat $1 | grep $2 | awk -F'=' '{ print $2 }'`
    echo $value
}


#解析json方法,使用的是服务器上带的python2.7
#有两个入参 1. 解析的文件 2. 表达式 "['province'][1]['name']"
function load_json(){
  file=$1
  expres="${2}"
  cat ${file} | /usr/bin/python2.7 -c "import json; import sys; obj=json.load(sys.stdin); print obj"${expres}".encode('utf-8')"
  exit $?
}
