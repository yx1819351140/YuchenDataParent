#!/bin/bash
#参数1 数据类型        geg
#参数2 sql文件名       gdelt_input_geg.sql


source $SCRIPT_HOME/env/common_setting.sh

/usr/bin/python3  $LIB_DIR/python/phoenix_collect.py