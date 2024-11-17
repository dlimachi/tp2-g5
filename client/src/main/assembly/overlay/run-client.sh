#!/bin/bash

PATH_TO_CODE_BASE=`pwd`
MAIN_JAR="client/target/tp2-g5-client-2024.2Q.jar"
java -jar "$PATH_TO_CODE_BASE/$MAIN_JAR" "$@"

