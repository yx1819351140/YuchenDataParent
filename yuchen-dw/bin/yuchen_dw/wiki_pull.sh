#!/bin/bash

source $SCRIPT_HOME/env/common_setting.sh


echo $TOOLS_DIR


$TOOLS_DIR/mongo_tools.sh export_data wikidata_ods wiki_data_v3_v200_0 export.json "all_item" '{"_id": {"$eq":"P1002"}}'


$TOOLS_DIR/hive_tools.sh import_data test test_0001 file:./export.json
