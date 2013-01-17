#!/bin/bash
##This script takes a log file as input and generates a log file for each client
##based on an unique client id. The files are stored in the ./log directory.
##
##Usage:
##    ./log_analyzer.sh <logfile>
##

FILENAME=$1
LOG_FOLDER="log"

function usage() {
    sed -n 's/^##\([^$]*\).*/\1/p' $0
}

if [ -z $FILENAME ] || [ ! -f $FILENAME ]; then
	usage
        exit
fi

if [ ! -d "$LOG_FOLDER" ]; then
    mkdir log
fi

cd log

echo "work in progress ..."

while read line
do 
	PROCESS=`echo $line | awk -F" " '{print substr($4,1,4)};'`
	echo $line >> "$FILENAME.$PROCESS"
done < ../$FILENAME

echo "work done!"
