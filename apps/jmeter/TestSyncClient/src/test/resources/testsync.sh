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