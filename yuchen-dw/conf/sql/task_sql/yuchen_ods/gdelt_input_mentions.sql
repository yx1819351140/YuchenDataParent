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



TRUNCATE TABLE mention_src_v2;
TRUNCATE TABLE mention_orc_v2temp;
load data inpath '${hivevar:gdelt_hdfs_path}' into table mention_src_v2;
insert into  mention_orc_v2temp partition(date_part) select * from mention_src_v2 where globaleventid!='';
insert into  mention_orc_v1 partition(date_part) select * from mention_orc_v2temp ;
insert into urlmention_hbase_v1 select concat(mask_hash(mentionidentifier),'_',globaleventid,'_',substr(mask_hash(*),0,8)) as key,* from mention_orc_v2temp;
insert into eventmention_hbase_v1  select concat(globaleventid,'_',mask_hash(mentionidentifier),'_',substr(mask_hash(*),0,8)) as key,* from mention_orc_v2temp;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'men',mentionidentifier,mask_hash(mentionidentifier),date_part,replace(current_date(),'-','') from mention_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,mention,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(mentionidentifier)),mentionidentifier,current_timestamp(),date_part  from mention_orc_v2temp;
