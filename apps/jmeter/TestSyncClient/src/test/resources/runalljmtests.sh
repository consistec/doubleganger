#! /bin/bash

# This script runs a all jmeter tests.
# This scirpt should be called before you commit your changes
# to the repository to test if all jmeter tests don't have errors.

# This two variables has to be changed on an other machine

JMETER_PATH=${JMETER_PATH:?"JMETER_PATH has to be set. "}
SYNC_FRAMEWORK_PATH=${SYNC_FRAMEWORK_PATH:?"SYNC_FRAMEWORK_PATH has to be set"}


TESTS=(JMeter-Last-Test-ADD-ADD-Conflict-With-Check
        JMeter-Last-Test-ADD-Conflict-With-Check JMeter-Last-Test-ADD-With-Check
        JMeter-Last-Test-Delete-With-Check JMeter-Last-Test-Update-With-Check JMeter-Performance-Test-100-Inserts
        JMeter-Performance-Test-ADD JMeter-Performance-Test-Compare-Inserts JMeter-Performance-Test-Compare-NumberOfClients
        JMeter-Performance-Test-Delete JMeter-Performance-Test-Update)

#TESTS_SIZE=${#TESTS[@]}

#echo $TESTS_SIZE

for i in "${TESTS[@]}"
    do
        ${JMETER_PATH}/jmeter -n -t ${SYNC_FRAMEWORK_PATH}/apps/jmeter/TestSyncClient/target/test-classes/jmeter-tests/${i}.jmx -l ${i}.jtl -j ${SYNC_FRAMEWORK_PATH}/apps/jmeter/TestSyncClient/target/test-classes/jmeter-tests/${i}.log
    done

# This didn't works for me (infinite loop) - Piotrek 
#i=1
#    until [ $i -gt ${TESTS_SIZE} ]
#    do
#        echo $i
#        ${JMETER_PATH}/jmeter -n -t ${SYNC_FRAMEWORK_PATH}/apps/jmeter/TestSyncClient/target/test-classes/jmeter-tests/${TESTS[$i]}.jmx -l ${TESTS[$i]}.jtl -j ${SYNC_FRAMEWORK_PATH}/apps/jmeter/TestSyncClient/target/test-classes/jmeter-tests/${TESTS[$i]}.log
#    done
