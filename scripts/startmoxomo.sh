#!/bin/bash

CURR=$PWD
cd /opt/moxomo/services/
nohup java  -Dname=moxomo -jar moxomo-springboot.jar > moxomo.log 2>&1 &
echo $! > .pid
sleep 1
cd $CURR