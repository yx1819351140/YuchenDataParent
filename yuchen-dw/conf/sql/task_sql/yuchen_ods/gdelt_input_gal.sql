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


TRUNCATE TABLE gal_src_v2;
TRUNCATE TABLE gal_orc_v2temp;
load data inpath  '${hivevar:gdelt_hdfs_path}'  into table gal_src_v2;
insert into  gal_orc_v2temp partition(date_part) select * from gal_src_v2 ;
insert into  gal_orc_v1 partition(date_part) select * from gal_orc_v2temp ;
insert into table gal_hbase_v1 select mask_hash(url),* from gal_orc_v2temp;
insert into table index_hbase_v1 select mask_hash(url),date_part,url,date_v1  from gal_orc_v2temp;
insert into table  mediacountry_hbase_v1 select mask_hash(a1.url),a2.fips10_4  from gal_orc_v2temp a1  left join  domain_countrycode_orc_v1 a2  on  a1.domain =a2.domain  where a2.fips10_4  is not null ;
insert into table  domainforget_orc_v1 select  a1.domain  from gal_orc_v2temp a1  where a1.domain  not in (select  distinct(domain)  from domain_countrycode_orc_v1 union select domain from  domainforget_orc_v1);
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'gal',url,mask_hash(url),date_part,replace(current_date(),'-','') from gal_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,gal,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(url)),url,current_timestamp(),date_part  from gal_orc_v2temp;
add jar /opt/cloudera/parcels/PHOENIX-5.0.0-cdh6.2.0.p0.1308267/lib/phoenix/phoenix-5.0.0-cdh6.2.0-hive.jar;
insert into table todayurl_hbase_v2_part1(rowid,date_part,url ,timev1, title,image,domainv1,outletname)  select mask_hash(url),date_part,url,date_v1,title,image,domain,outletname from gal_orc_v2temp ;
insert into table todayurl_hbase_v2_part2(rowid,media_country) select mask_hash(a1.url),a2.fips10_4  from gal_orc_v2temp a1  left join  domain_countrycode_orc_v1 a2  on  a1.domain =a2.domain  where a2.fips10_4  is not null ;