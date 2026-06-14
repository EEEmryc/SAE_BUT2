#!/bin/sh
set -eu

mkdir -p /app/uploads/resources
chown -R learnhub:learnhub /app/uploads

exec gosu learnhub sh -c 'java $JAVA_OPTS -jar app.jar'
