#!/bin/bash
####################################################
# 作者:liyi
# 用途:gdelt新闻入库采集程序
#
####################################################
#参数1 数据类型        geg
#参数2 sql文件名       gdelt_input_geg.sql

source $SCRIPT_HOME/env/common_setting.sh

#gdelt的hdfs路径
export GDELT_HDFS_PATH=hdfs:///data/gdelt/files/json
#yesterday=`$TOOLS_DIR/date_tools.sh yesterday`
thismon=`$TOOLS_DIR/date_tools.sh thismonth`
echo '执行'${1}'数据导入'
hdfs_path=$GDELT_HDFS_PATH/${1}/$thismon
echo '数据来源路径'$hdfs_path

#文件数量统计
filesum=`hdfs dfs -ls $hdfs_path |  wc -l`
echo $filesum
#新闻数据入库
if [ $filesum  -ge  1 ];
then
$TOOLS_DIR/hive_tools.sh execute_sql_file $TASK_SQL/yuchen_ods/${2} gdelt_hdfs_path=$hdfs_path
fi