#!/bin/bash

##############################################
#
#       功能描述: monitor stop
#       修改者: HQP
#       修改时间: 2022.12.01
#       版本: v1.0.0
#
##############################################

# shellcheck disable=SC2009
ps -ef | grep yuchen-monitor | grep -v grep | cut -c 9-16 | xargs kill -s 9

