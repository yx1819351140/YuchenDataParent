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


TRUNCATE TABLE ngram_src_v2;
TRUNCATE TABLE ngram_orc_v2temp;
load data inpath '${hivevar:gdelt_hdfs_path}'  into table ngram_src_v2;
insert into  ngram_orc_v2temp partition(date_part) select * from ngram_src_v2 where url != '' and date_part != "__HIVE_DEFAULT_PARTITION__";
insert into  ngram_orc_v1 partition(date_part) select * from ngram_orc_v2temp ;
insert into table ngram_hbase_v1 select mask_hash(url),* from ngram_orc_v2temp;
insert into table index_hbase_v1 select mask_hash(url),date_part,url,date_v1  from ngram_orc_v2temp;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'ngr',url,mask_hash(url),date_part,replace(current_date(),'-','') from ngram_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,ngram,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(url)),url,current_timestamp(),date_part  from ngram_orc_v2temp;
add jar /opt/cloudera/parcels/PHOENIX-5.0.0-cdh6.2.0.p0.1308267/lib/phoenix/phoenix-5.0.0-cdh6.2.0-hive.jar;
insert into table todayurl_hbase_v2_part3(rowid,date_part,url ,timev1,lang,context)  select mask_hash(url),date_part,url,date_v1, lang,context  from ngram_orc_v2temp ;
