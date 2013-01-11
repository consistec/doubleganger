#!/bin/bash
##This script takes a log file as input and generates a log file for each client
##based on their hash number. The files are stored in the current directory.
##
##Usage:
##    ./log_analyzer.sh <logfile>
##

FILENAME=$1

function usage() {
    sed -n 's/^##\([^$]*\).*/\1/p' $0
}

if [ -z $FILENAME ] || [ ! -f $FILENAME ]; then
	usage
        exit
fi

cd log

echo "in progress ..."

while read line
do 
	PROCESS=`echo $line | awk -F" " '{print substr($4,1,4)};'`
	echo $line >> "$FILENAME.$PROCESS"
done < ../$FILENAME

echo "work done!"
