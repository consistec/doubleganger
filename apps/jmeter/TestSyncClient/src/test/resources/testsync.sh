#!/bin/bash

# script which is called from jmeter-tests to run the TestSyncClient

FILE="${basedir}/target/TestSyncClientFull.jar"
# DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# ------------------------------------------
# ---------- arguments description ---------
# ------------------------------------------
# $1 = server properties
# $2 = thread number
# $3 = log file name
# $4 = number of retries to apply changes
# $5 = number to retry the whole sync

echo "calling java -jar $FILE $1 $2 $3 $4 $5"

java $DEBUG -jar $FILE $1 $2 $3 $4 $5 $6