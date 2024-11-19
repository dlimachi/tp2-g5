#!/bin/bash

ADDITIONAL_PARAM="-Dquery=5"

sh client/src/main/assembly/overlay/run-client.sh "$@" "$ADDITIONAL_PARAM"