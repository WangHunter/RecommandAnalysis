#!/bin/sh
today=`date +%Y%m%d`
fivedaysago=`date -d "5 days ago" +%Y%m%d`
#创建备份文件夹
if [ ! -d "/data/db/backup/$today" ] 
then
mkdir /data/db/backup/$today
fi

#删除5天前的备份数据
if [ -d "/data/db/backup/$fivedaysago" ]
then
echo "开始删除备份数据:"$/data/db/backup/$fivedaysago
remove /data/db/backup/$fivedaysago
fi
#数据库备份
mongodump -h 127.0.0.1:27017 -d recommandDbTest -o /data/db/backup/$today/
