#!/bin/bash

PATH_TO_CODE_BASE=`pwd`
MAIN_JAR="server/target/tp2-g5-server-2024.2Q.jar"
java -jar "$PATH_TO_CODE_BASE/$MAIN_JAR" "$@"
