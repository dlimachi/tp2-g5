#!/bin/bash

ADDITIONAL_PARAM="-Dquery=3"

sh client/src/main/assembly/overlay/run-client.sh "$@" "$ADDITIONAL_PARAM"