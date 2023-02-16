#!/bin/bash

source $SCRIPT_HOME/env/common_setting.sh

#################################################
# 功能: 监控namenode状态,发生主备切换及时告警
# 作者: 肖振男
# 日期: 2023-02-16
#################################################

url="https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=cdecf27b-f22f-4167-8ca3-bce1280e50d3"
currentActive=`hdfs haadmin -getAllServiceState | grep active | awk '{print $1}'`
if [[ $currentActive == datanode01* ]]; then
        echo "current active is datanode01"
else
        echo -e "current active is datanode02"
        echo -e "start report active node change event"
        curl -X POST -H "Content-Type: application/json" -d '{"msgtype": "markdown","markdown": {"content": "<font color=\"red\">**大数据组件告警**</font>\n>**告警组件: **<font>HDFS</font>\n>**告警信息: **<font color=\"comment\">NameNode主备切换</font>\n>**Current Active: **<font color=\"comment\"> datanode02</font>"}}' $url
fi
#echo $currentActive

echo -e "\nnamenode state scan is over"
