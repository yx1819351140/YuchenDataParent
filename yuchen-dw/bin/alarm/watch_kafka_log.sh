#!/bin/bash

export SCRIPT_HOME=/home/bigdata/xiaozhennan/dw_script
source $SCRIPT_HOME/env/common_setting.sh


kafka_broker_restart()
{
	logger_info "开始进行kafka broker 重启, 当前节点: $1"
	logger_info "kafka borker id: $2"
	curl -u xiaozhennan:yc123456 -X POST -H "Content-Type: application/json" -d '{"items":["'"${2}"'"]}' "http://datanode01:7180/api/v32/clusters/Cluster%201/services/kafka/roleCommands/restart"
	logger_info "kafka broker 重启完成, 当前节点: $1"
}
nodes="datanode01 datanode02 datanode03 datanode04"
datanode01=0
datanode02=0
datanode03=0
datanode04=0

interval_time="45m"

while true
do
logger_info "开始进行kafka broker 假死状态巡检！"
for node in $nodes
do
count=`ssh $node "cat /var/log/kafka/kafka-broker-$node.log | wc -l"`
if [ $node = "datanode01" ];then
	logger_info "当前节点: $node 本次日志offset: $count 上次日志offset: $datanode01"
	if [ $count = $datanode01 ];then
		logger_err "当前节点: $node ,已经超过 $interval_time 没有日志输出！"
		kafka_broker_restart $node "kafka-KAFKA_BROKER-45503d9a6784a2dddf0787fb7a106988"
	fi
	datanode01=$count
fi
if [ $node = "datanode02" ];then
	logger_info "当前节点: $node 本次日志offset: $count 上次日志offset: $datanode02"
	if [ $count = $datanode02 ];then
		logger_err "当前节点: $node, 已经超过 $interval_time 没有日志输出！"
		kafka_broker_restart $node "kafka-KAFKA_BROKER-3a23102c1100629942d0ffe5ed300aae"
	fi
	datanode02=$count
fi
if [ $node = "datanode03" ];then
	logger_info "当前节点: $node 本次日志offset: $count 上次日志offset: $datanode03"
	if [ $count = $datanode03 ];then
		logger_err "当前节点: $node ,已经超过 $interval_time 没有日志输出！"
		kafka_broker_restart $node "kafka-KAFKA_BROKER-30edeb473c60c2ddc5608f0825168b37"
	fi
	datanode03=$count
fi
if [ $node = "datanode04" ];then
	logger_info "当前节点: $node 本次日志offset: $count 上次日志offset: $datanode04"
	if [ $count = $datanode04 ];then
		logger_err "当前节点: $node ,已经超过 $interval_time 没有日志输出！"
		kafka_broker_restart $node "kafka-KAFKA_BROKER-5ba76039ea6d3e6d50d22116ad3f2431"
	fi
	datanode04=$count
fi
sleep 1s
done
logger_info "kafka broker 假死状态巡检完成！"
sleep $interval_time
done

