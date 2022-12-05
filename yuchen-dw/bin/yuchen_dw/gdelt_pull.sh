#!/bin/bash


source $SCRIPT_HOME/env/common_setting.sh

echo $TOOLS_DIR
 

yesterday=`$TOOLS_DIR/date_tools.sh yesterday`
#新闻数据入库
$TOOLS_DIR/hive_tools.sh execute_sql_file $TASK_SQL/yuchen_ods/todayurl_hbase_v2.sql yesterday="${yesterday}",num=10,tableName=my_test_20221131
