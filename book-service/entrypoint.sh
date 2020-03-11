#!/bin/bash

set -ex

PATH=$PATH:`pwd`

#JAVA_OPTS=""
AUTO_JAVA_OPTS=""
DEFAULT_JAVA_HEAP_PERCENT=80

# not disable_auto_heap
if [ -z "$DISABLE_JAVA_AUTO_HEAP" ]; then

  # If CONTAINER_MEM_MB is set, the heap is set to a percent of that
  # value equal to JAVA_HEAP_PERCENT; otherwise it uses the default
  # memory settings.
  if [ ! -z "$CONTAINER_MEM_MB" ]; then

    if [ -z "$JAVA_HEAP_PERCENT" ]; then
        JAVA_HEAP_PERCENT=$DEFAULT_JAVA_HEAP_PERCENT
    fi

    MEM_JAVA_MB=$(($CONTAINER_MEM_MB * $JAVA_HEAP_PERCENT / 100))

    echo "[Start Script] Configuring JVM max heap to ${JAVA_HEAP_PERCENT}% [${MEM_JAVA_MB}m]"
    AUTO_JAVA_OPTS="-Xmx${MEM_JAVA_MB}m"
  fi
fi

if [ ! -z "$AUTO_JAVA_OPTS" ]; then
  JAVA_OPTS="$AUTO_JAVA_OPTS"
fi

if [ -z "$JAVA_OPTS" ]; then
  JAVA_OPTS="-XX:MaxJavaStackTraceDepth=50"
else
  JAVA_OPTS="-XX:MaxJavaStackTraceDepth=50 $JAVA_OPTS"
fi

export JAVA_OPTS="$JAVA_OPTS"
echo "[Start Script] Starting app"

# shellcheck disable=SC2086
#java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /application/bin/app.jar
java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /application/bin/app.jar --spring.config.location=classpath:/default.properties,classpath:/application.yml,file:/application/config/application.yml

exec "$@"