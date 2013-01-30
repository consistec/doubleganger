#! /bin/bash

# This script runs a specified jmeter test for x times.
# The name and number of tests is passed through the arguments.
# This is practical if a specified test has an error which can not
# always be reproduced.
# if the number is not set as argument, then the default value
# of 5 will be used.

# This two variables has to be changed on an other machine
JMETER_PATH="/home/marcel/dev/svn-workspace/jmeter_v2_8_RC1/bin"
SYNCBIB_PATH="/home/marcel/dev/workspaces/syncframework"
DEFAULT_XTIMES_NUMBER=2
XTIMES_NUMBER=${DEFAULT_XTIMES_NUMBER}

echo ${XTIMES_NUMBER}

if [ $# -gt 2 -o $# -eq 0 ]; then
    echo "Usage: runxtimejmtest filename [xtimes_number]!"
    exit 1
else
	if [ $# -eq 2 ]; then
		XTIMES_NUMBER=$2
	fi
    i=1
    until [ $i -gt ${XTIMES_NUMBER} ]
    do
    ${JMETER_PATH}/jmeter -n -t ${SYNCBIB_PATH}/apps/jmeter/TestSyncClient/target/test-classes/jmeter-tests/$1.jmx -l $1.jtl
	i=$[$i+1]
    done
fi
