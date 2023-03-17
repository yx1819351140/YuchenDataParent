add jar /opt/cloudera/parcels/CDH-6.2.0-1.cdh6.2.0.p0.967373/lib/hive/lib/hive-hcatalog-core-2.3.9.jar;
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrick;
set hive.exec.max.dynamic.partitions=20480;
set hive.exec.max.dynamic.partitions.pernode=20480;
set  hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
set hive.execution.engine=spark;
set mapred.job.queue.name=liyi.gdelttestv1;
set spark.master=yarn-cluster;
set spark.executor.instances=20;


TRUNCATE TABLE geg_src_v2;
TRUNCATE TABLE geg_orc_v2temp;
load data  inpath  '${hivevar:gdelt_hdfs_path}' into table geg_src_v2;
insert into table geg_orc_v2temp partition(date_part) select  get_json_object(line,'$.date') as  date_v1,get_json_object(line,'$.url') as url,get_json_object(line,'$.lang') as  lang,get_json_object(line,'$.polarity') as  polarity,get_json_object(line,'$.magnitude') as  magnitude,get_json_object(line,'$.score') as score,get_json_object(line,'$.entities') as entities,get_json_object(line,'$.date_part') as date_part from geg_src_v2 where get_json_object(line,'$.url')!='';
insert into table geg_orc_v1 partition(date_part) select * from geg_orc_v2temp;
insert into table geg_hbase_v1 select mask_hash(url),date_v1,date_part,url,lang,polarity,magnitude,score,entities   from geg_orc_v2temp;
insert into table index_hbase_v1 select mask_hash(url),date_part,url,date_v1  from geg_orc_v2temp;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'geg',url,mask_hash(url),date_part,replace(current_date(),'-','') from geg_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,geg,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(url)),url,current_timestamp(),date_part  from geg_orc_v2temp;
