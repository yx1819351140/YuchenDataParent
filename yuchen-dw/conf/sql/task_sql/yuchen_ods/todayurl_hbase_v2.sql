add jar /opt/cloudera/parcels/CDH-6.2.0-1.cdh6.2.0.p0.967373/lib/hive/lib/hive-hcatalog-core-2.3.9.jar;
set hive.execution.engine=spark;
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrick;
set hive.exec.max.dynamic.partitions.pernode=10000;
set hive.exec.max.dynamic.partitions=10000;
set hive.exec.max.created.files=10000;
set mapred.map.tasks.speculative.execution=true;
set mapred.reduce.tasks.speculative.execution=true;
set spark.blacklist.enabled=false;
set mapreduce.map.memory.mb=10240;
set mapreduce.reduce.memory.mb=10240;
set mapreduce.map.java.opts=-Xmx10240m;
set mapreduce.reduce.java.opts=-Xmx10240m;
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrick;
set hive.exec.max.dynamic.partitions=20480;
set hive.exec.max.dynamic.partitions.pernode=20480;
set mapred.max.split.size=256000000;
set mapred.min.split.size.per.node=256000000;
set mapred.min.split.size.per.rack=256000000;
set  hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
set hive.exec.reducers.bytes.per.reducer=256000000;
set hive.mapjoin.smalltable.filesize=55000000;
set hive.auto.convert.join = false;
create table ${hivevar:tableName} as select mask_hash(a1.url),a2.fips10_4  from gal_orc_v2temp a1  left join  domain_countrycode_orc_v1 a2  on  a1.domain =a2.domain  where a2.fips10_4  is not null and 10 = '${hivevar:num}';