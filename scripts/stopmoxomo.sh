#!/bin/bash

if [[ -f /opt/moxomo/services/.pid ]]; then
        kill `cat /opt/moxomo/services/.pid`;
        mypid=`cat /opt/moxomo/services/.pid`;
        while [[ `ps -p $mypid > /dev/null;echo $?` -eq '0' ]]; do
                echo -n '.';
                sleep 1;
        done
        rm -f  /opt/moxomo/services/.pid;
fi

echo STOPPING DONE