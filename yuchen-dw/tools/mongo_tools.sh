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

export_cmd="mongoexport -u $mongo_user -h $mongo_server -p $mongo_password --authenticationDatabase $mongo_auth_db --type=json --jsonFormat=canonical --noHeaderLine "

#执行sql
exec_export_data ()
{
database="${1}"
collection="${2}"
export_file="${3}"
fields="${4}"
query="{}"
if [ -n "${@:5}" ]; then
	echo "queryyy""${@:5}"
	query="${@:5}"
fi
queryFile=`mktemp -t mongo_query.json.XXXXXXXXXXXX`
echo $query > $queryFile
echo "fields:  $fields"
export_cmd=$export_cmd"-d $database -c $collection --queryFile $queryFile -o $export_file -f $fields"
echo $export_cmd
#export_cmd=${export_cmd%--*}
logger_info "开始进行mongo数据导出, 数据库: [$database], 数据表: [$collection], 查询条件: [$query]"
$export_cmd
flag_num=$?
rm $queryFile
logger_info "导出mongo数据完成, 数据导出到: [$export_file]"
return $flag_num
	
echo "exec export data"
}

exec_import_data ()
{
echo "exec import data"
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

