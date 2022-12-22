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

TRUNCATE TABLE gemg_src_v2;
TRUNCATE TABLE gemg_orc_v2temp;
load data inpath '${hivevar:gdelt_hdfs_path}' into table gemg_src_v2;
insert into table gemg_orc_v2temp partition(date_part)
select  get_json_object(line,'$.url'),get_json_object(line,'$.publishtime'),get_json_object(line,'$.keyword'), get_json_object(line,'$abstract'),get_json_object(line,'$.ogtype'), get_json_object(line,'$.date_part') from gemg_src_v2  ;
insert into table gemg_orc_v1 partition(date_part) select * from gemg_orc_v2temp;
insert into table gemg_hbase_v1 select mask_hash(url),*   from gemg_orc_v2temp;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'gemg',url,mask_hash(url),date_part,replace(current_date(),'-','') from gemg_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,gemg,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(url)),url,current_timestamp(),date_part  from gemg_orc_v2temp;
