#!/bin/bash

ADDITIONAL_PARAM="-Dquery=4"

sh client/src/main/assembly/overlay/run-client.sh "$@" "$ADDITIONAL_PARAM"