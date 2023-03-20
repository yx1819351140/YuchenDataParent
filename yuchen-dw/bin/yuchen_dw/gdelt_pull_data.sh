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

thismon=`$TOOLS_DIR/date_tools.sh thismonth`
lastmon=`$TOOLS_DIR/date_tools.sh lastmonth`

echo '执行'${1}'数据导入'
hdfs_path=$GDELT_HDFS_PATH/${1}/$thismon
lastmon_hdfs_path=$GDELT_HDFS_PATH/${1}/$lastmon

#获取当前日期：如果当前日期小于3号，移动上月目录下的数据到本月目录下
lastmon_filesum=`hdfs dfs -ls $lastmon_hdfs_path |  wc -l`
if [ $lastmon_filesum  -ge  1 ];
then
hdfs dfs -mv $lastmon_hdfs_path/* $hdfs_path
echo '执行'${1}'上月目录文件移动'
fi


echo '数据来源路径'$hdfs_path

#文件数量统计
filesum=`hdfs dfs -ls $hdfs_path |  wc -l`
echo $filesum
#新闻数据入库
if [ $filesum  -ge  1 ];
then
$TOOLS_DIR/hive_tools.sh execute_sql_file $TASK_SQL/yuchen_ods/${2} gdelt_hdfs_path=$hdfs_path
fi