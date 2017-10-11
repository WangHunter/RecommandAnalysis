#!/bin/sh
time=`date "+%Y-%m-%d %H:%M:%S"`
echo $time
redis-cli<<EOF
SAVE
EOF
