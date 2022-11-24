#!/bin/bash


source ~/.bash_profile
echo $SCRIPT_HOME
source /home/bigdata/apps/xiaozhennan/dw_script/env/common_setting.sh


echo "TOOLS_DIR"$TOOLS_DIR
$TOOLS_DIR/hive_tools.sh executor_sql "show databases;"
exit 0
