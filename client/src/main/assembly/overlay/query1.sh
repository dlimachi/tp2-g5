#!/bin/bash

ADDITIONAL_PARAM="-Dquery=1"

sh client/src/main/assembly/overlay/run-client.sh "$@" "$ADDITIONAL_PARAM"