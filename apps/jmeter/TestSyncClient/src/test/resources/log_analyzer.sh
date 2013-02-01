#!/bin/bash

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
