#!/bin/bash

source ${SCRIPT_HOME}/env/common_setting.sh
############################################################
#
#       功能描述: mongo操作工具
#       修改者: 肖振男
#       修改时间: 2019.1.3
#       版本: v1.0
#
#
#
#############################################################



run_flag=0
#默认的导出mongo配置, 如果需要链接其它mongo, 需要在脚本中 export mongo_profile=xxxx.properties
mprofile=$CONF_DIR/$PROJECT_ENV/mongo.properties
if [ ! -n "$mongo_profile" ]; then
	mporfile=$mongo_profile
fi
#beeline_url=`$TOOLS_DIR/load_config.sh $CONF_DIR/$PROJECT_ENV/hive.properties beeline_url`
mongo_server=`$TOOLS_DIR/load_config.sh $mprofile mongo.server`
mongo_port=`$TOOLS_DIR/load_config.sh $mprofile mongo.port`
mongo_password=`$TOOLS_DIR/load_config.sh $mprofile mongo.password`
mongo_user=`$TOOLS_DIR/load_config.sh $mprofile mongo.user`
mongo_auth_db=`$TOOLS_DIR/load_config.sh $mprofile mongo.authdb`

#定义默认导出的数据文件格式
export_type=json
#默认的导出命令
export_cmd="mongoexport -u $mongo_user -h $mongo_server -p $mongo_password --authenticationDatabase $mongo_auth_db --jsonFormat=canonical --noHeaderLine "

#执行导出数据
exec_export_data ()
{
#数据库
database="${1}"
#表
collection="${2}"
#导出文件
export_file="${3}"
#导出字段
fields="${4}"
#默认查询语句
query="{}"
if [ -n "${@:5}" ]; then
	echo "query:""${@:5}"
	query="${@:5}"
fi
# shellcheck disable=SC2006
# 根据导出文件后缀区分导出类型,支持json和csv
export_type=`echo "${export_file}" | grep -q -E '\.json$' && echo "json" || echo "csv"`
#创建查询语句临时文件
queryFile=`mktemp -t mongo_query.json.XXXXXXXXXXXX`
#查询语句写入到查询语句的临时文件
echo $query > $queryFile
echo "fields:  $fields"
export_cmd=$export_cmd"--type=$export_type -d $database -c $collection --queryFile $queryFile -o $export_file -f $fields"
echo $export_cmd
#export_cmd=${export_cmd%--*}
logger_info "开始进行mongo数据导出, 数据库: [$database], 数据表: [$collection], 查询条件: [$query]"
$export_cmd
flag_num=$?
rm $queryFile
logger_info "导出mongo数据完成, 数据导出到: [$export_file]"
return $flag_num
}

exec_import_data ()
{
echo "Importing data to mongo is not currently supported!"
}




case $1 in
	"export_data")
		exec_export_data "${2}" "${3}" "${4}" "${5}" "${@:6}"
		run_flag=$?
    ;;
	"import_data")
		exec_import_data "${2}" "${@:3}"
		
		run_flag=$?
    ;;
	*)
		echo "没有匹配的操作"
    ;;
esac


if [ $run_flag -eq 0 ]; then
    exit 0
else
    exit 1
fi

