#!/bin/bash

CURR=$PWD
cd /opt/moxomo/services/
test -e "/tmp/moxomo-springboot.jar" && cp /tmp/moxomo-springboot.jar .
nohup  /opt/moxomo/java/jdk1.8.0_202/bin/java -Dname=moxomo -jar moxomo-springboot.jar --jasypt.encryptor.password=Reneg@te85  > moxomo.log 2>&1 &
echo $! > .pid
sleep 1
cd $CURR