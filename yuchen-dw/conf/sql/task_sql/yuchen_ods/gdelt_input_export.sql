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

TRUNCATE TABLE export_src_v2;
load data inpath '${hivevar:gdelt_hdfs_path}'  into table export_src_v2;
insert into  export_orc_v1 partition(date_part) select * from export_src_v2 where globaleventid!='';
insert into table event_hbase_v1 select * from export_src_v2;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'exp',globaleventid,mask_hash(sourceurl),date_part,replace(current_date(),'-','') from export_src_v2;
insert into table  monthevent_hbase_v1(hashkey,export,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',globaleventid),current_timestamp(),date_part  from export_src_v2;
