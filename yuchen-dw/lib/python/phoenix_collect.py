#!/usr/bin/env python
# -*- encoding: utf-8 -*-
'''
@File    :   cn_phoeneixv1.py.py    
@Contact :   liyi@yuchen.net.cn
@License :   (C)Copyright 2017-2018, Liugroup-NLPR-CASIA

@Modify Time      @Author    @Version    @Desciption
------------      -------    --------    -----------
2022/5/15 15:04   yi.lee      1.00         None
'''

import jaydebeapi
import time
import datetime
conn = jaydebeapi.connect('org.apache.phoenix.jdbc.PhoenixDriver', \
                           'jdbc:phoenix:datanode01:2181',["",""], \
                           '/opt/cloudera/parcels/PHOENIX-5.0.0-cdh6.2.0.p0.1308267/lib/phoenix/phoenix-5.0.0-cdh6.2.0-client.jar')
# {'phoenix.schema.isNamespaceMappingEnabled': 'true'},

ti = time.time()
curs = conn.cursor()
conn.jconn.setAutoCommit(True);
ti = time.time()
#curs.execute("SELECT * FROM PHOENIX_TABLE_T31")
#curs.execute("upsert into PHOENIX_TABLE_T31(\"a1\" ,\"a2\",\"a3\") values(\'13\',\'13\',\'13\')")
#aa=curs.execute("upsert into PHOENIX_TABLE_T31(\"a1\" ,\"a2\",\"a3\",\"a4\") values('1333','33','55','55') ")
curs.execute("UPSERT  into  TODAYURL_HBASE_V3(\"rowid\",DATE_PART,URL,TITLE ,IMAGE,OUTLETNAME,DOMAINV1)  select \"rowid\",DATE_PART,URL,TITLE ,IMAGE,OUTLETNAME,DOMAINV1  from TODAYURL_HBASE_V2_PART1")
curs.execute("UPSERT  into  TODAYURL_HBASE_V3(\"rowid\",MEDIA_COUNTRY)  select \"rowid\",MEDIA_COUNTRY  from TODAYURL_HBASE_V2_PART2")
curs.execute("UPSERT into TODAYURL_HBASE_V3(\"rowid\",DATE_PART,URL,TIMEV1,LANG,CONTEXT)  select \"rowid\",DATE_PART,URL,TIMEV1, LANG,CONTEXT  from TODAYURL_HBASE_V2_PART3 ")
curs.execute("UPSERT into TODAYURL_HBASE_V3(\"rowid\",DATE_PART,URL,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE)  select \"rowid\",DATE_PART,URL,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE  from TODAYURL_HBASE_V2_PART4 ")
conn.commit()
print("完成phoenix刷新")
aa1=curs.execute("DELETE  FROM TODAYURL_HBASE_V2_PART1")
aa2=curs.execute("DELETE  FROM TODAYURL_HBASE_V2_PART2")
aa3=curs.execute("DELETE  FROM TODAYURL_HBASE_V2_PART3")
aa4=curs.execute("DELETE  FROM TODAYURL_HBASE_V2_PART4")
bb=conn.commit()
print("完成phoenix删除")
curs.execute("upsert into todayurl_hbase_v2(\"rowid\",DATE_PART,URL,MEDIA_COUNTRY,TIMEV1,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE,LANG,CONTEXT,TITLE,IMAGE,OUTLETNAME,DOMAINV1)  select \"rowid\",DATE_PART,URL,MEDIA_COUNTRY,TIMEV1,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE,LANG,CONTEXT,TITLE,IMAGE,OUTLETNAME,DOMAINV1 from todayurl_hbase_v3 where title is not null and lang is not null and yc_news_catalogue is not null ")
conn.commit()
print("完成数据收集")
curs.execute("DELETE  FROM todayurl_hbase_v3 where title is not null and lang is not null and yc_news_catalogue is not null ")
conn.commit()
print("删除收集的原始数据")
dayv1 = (datetime.date.today()-datetime.timedelta(days=10)).strftime("%Y%m%d")
dayv2 = (datetime.date.today()-datetime.timedelta(days=10)).strftime("%Y-%m-%d")
strsql = "DELETE  FROM todayurl_hbase_v3 where DATE_PART<=\'"+dayv1+ "\' or TIMEV1<=\'"+dayv2 +"\'" 
curs.execute(strsql)
conn.commit()
print("删除10天前数据")

# UPSERT  into  TODAYURL_HBASE_V2("rowid",DATE_PART,URL,TIMEV1,TITLE ,IMAGE,OUTLETNAME,DOMAINV1)  select "rowid",DATE_PART,URL,TIMEV1,TITLE ,IMAGE,OUTLETNAME,DOMAINV1  from TODAYURL_HBASE_V2_PART1")
# UPSERT  into  TODAYURL_HBASE_V2("rowid",MEDIA_COUNTRY)  select "rowid",MEDIA_COUNTRY  from TODAYURL_HBASE_V2_PART2;
# UPSERT into TODAYURL_HBASE_V2("rowid",DATE_PART,URL,TIMEV1,LANG,CONTEXT)  select "rowid",DATE_PART,URL,TIMEV1, LANG,CONTEXT  from TODAYURL_HBASE_V2_PART3 ;
# UPSERT into TODAYURL_HBASE_V2("rowid",DATE_PART,URL,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE)  select "rowid",DATE_PART,URL,YC_NEWS_CATALOGUE,YC_INVOLVE_COUNTRY,V1_5TONE_TONE  from TODAYURL_HBASE_V2_PART4")

#aa=curs.execute("delete from PHOENIX_TABLE_T31")
# bb=conn.commit()
# print(aa)
# print(bb)
# # curs.execute("SELECT * FROM PHOENIX_TABLE_T31")
# print(curs.fetchall())
# print(time.time()-ti)