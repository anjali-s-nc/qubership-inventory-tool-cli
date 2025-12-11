#!/bin/sh
cd /app || exit 1
java \
    -Xmx512m -Dlogback.configurationFile=./logback.xml \
    -jar ./inventory-tool.jar \
    ci-exec \
    --dockerMode=true \
    "$@"
