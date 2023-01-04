#!/bin/bash
####################################################
# 作者:xiaozhennan
# 用途:数据仓库脚手架样例程序
#
####################################################
source $SCRIPT_HOME/env/common_setting.sh
echo $TOOLS_DIR

#执行mongo数据导入到hive
#$TOOLS_DIR/mongo_tools.sh export_data wikidata_ods wiki_data_v3_v200_0 export.json "all_item" '{"_id": {"$eq":"P1002"}}'

#执行hive导入数据
#$TOOLS_DIR/hive_tools.sh import_data test test_0001 file:./export.json

#yesterday=`$TOOLS_DIR/date_tools.sh yesterday`
#执行HiveSql
#$TOOLS_DIR/hive_tools.sh execute_sql_file $TASK_SQL/yuchen_ods/todayurl_hbase_v2.sql yesterday="${yesterday}",num=10,tableName=my_test_20221131
