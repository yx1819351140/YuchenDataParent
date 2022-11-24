#!/bin/bash




curl -u xiaozhennan:yc123456 -X POST -H "Content-Type: application/json" -d '{"items":["'"${1}"'"]}' "http://datanode01:7180/api/v32/clusters/Cluster%201/services/kafka/roleCommands/restart"

