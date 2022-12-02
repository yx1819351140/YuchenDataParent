#!/bin/bash

##############################################
#
#       功能描述: 执行monitor
#       修改者: HQP
#       修改时间: 2022.12.01
#       版本: v1.0.0
#
##############################################

# start时间
start01=`date "+%Y-%m-%d %H:%M:%S"`
# timeStamp01=`date -d "$start01" +%s`

nohup java -jar \
-Dspring.config.location=../yuchen-monitor/src/main/resources/application-dev.yml \
../yuchen-monitor/target/yuchen-monitor-1.0.0.jar \
> ../logs/monitor-${start01}.log 2>&1 &



