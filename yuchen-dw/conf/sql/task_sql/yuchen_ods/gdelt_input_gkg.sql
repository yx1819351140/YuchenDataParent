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


TRUNCATE TABLE gkg_src_v2;
TRUNCATE TABLE gkg_orc_v2temp;
load data inpath '${hivevar:gdelt_hdfs_path}' into table gkg_src_v2;
insert into table gkg_orc_v2temp partition(date_part)  select  get_json_object(line,'$.url'),get_json_object(line,'$.gkgrecordid'),get_json_object(replace(line,'v2.1date','v2_1date'),'$.v2_1date'),get_json_object(line,'$.v2sourcecollectionidentifier'), get_json_object(line,'$.v2sourcecommonname'),get_json_object(line,'$.v2documentidentifier'),get_json_object(replace(line,'v2.1counts','v2_1counts'),'$.v2_1counts'), get_json_object(line,'$.v2enhancedthemes'), get_json_object(line,'$.v2enhancedlocations'),  get_json_object(line,'$.v2enhancedpersons'),  get_json_object(line,'$.v1organizations'),  get_json_object(line,'$.v2enhancedorganizations'),get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.tone'),get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.positivescore'), get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.negativescore'), get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.polarity'),  get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.activityreferencedensity'),  get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.selfgroupreferencedensity'), get_json_object(replace(line,'v1.5tone','v1_5tone'),'$.v1_5tone.wordcount'), get_json_object(replace(line,'v2.1enhanceddates','v2_1enhanceddates'),'$.v2_1enhanceddates'), get_json_object(replace(line,'v2.1sharingimage','v2_1sharingimage'),'$.v2_1sharingimage'),get_json_object(replace(line,'v2.1relatedimages','v2_1relatedimages'),'$.v2_1relatedimages'), get_json_object(replace(line,'v2.1socialimageembeds','v2_1socialimageembeds'),'$.v2_1socialimageembeds'),get_json_object(replace(line,'v2.1socialvideoembeds','v2_1socialvideoembeds'),'$.v2_1socialvideoembeds'), get_json_object(replace(line,'v2.1quotations','v2_1quotations'),'$.v2_1quotations'),get_json_object(replace(line,'v2.1allnames','v2_1allnames'),'$.v2_1allnames'), get_json_object(replace(line,'v2.1amounts','v2_1amounts'),'$.v2_1amounts'),get_json_object(replace(line,'v2.1translationinfo','v2_1translationinfo'),'$.v2_1translationinfo'), get_json_object(line,'$.v2extrasxml'), get_json_object(line,'$.yc_involve_country'),get_json_object(line,'$.yc_news_catalogue') ,get_json_object(line,'$.date_part') from gkg_src_v2  ;
insert into table gkg_orc_v1 partition(date_part) select * from gkg_orc_v2temp;
insert into table gkg_hbase_v1 select mask_hash(url),*   from gkg_orc_v2temp;
insert into table index_hbase_v2 select mask_hash(url),date_part,url  from gkg_orc_v2temp;
insert into table inserttime_orc_v1  partition(date_part)   select current_timestamp(),'gkg',url,mask_hash(url),date_part,replace(current_date(),'-','') from gkg_orc_v2temp;
insert into table  monthurl_hbase_v1(hashkey,url,gkg,date_part) select concat(replace(substr(current_timestamp(),0,7),'-',''),'_',mask_hash(url)),url,current_timestamp(),date_part  from gkg_orc_v2temp;
add jar /opt/cloudera/parcels/PHOENIX-5.0.0-cdh6.2.0.p0.1308267/lib/phoenix/phoenix-5.0.0-cdh6.2.0-hive.jar;
insert into table todayurl_hbase_v2_part4(rowid,date_part,url ,yc_news_catalogue,yc_involve_country,v1_5tone_tone)  select mask_hash(url),date_part,url,yc_news_catalogue,yc_involve_country,v1_5tone_tone from gkg_orc_v2temp ;
