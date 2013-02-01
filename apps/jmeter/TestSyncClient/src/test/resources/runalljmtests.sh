#! /bin/bash

###
# #%L
# doppelganger
# %%
# Copyright (C) 2011 - 2013 consistec GmbH
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the 
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public 
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###

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
