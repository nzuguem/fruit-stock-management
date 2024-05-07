#!/usr/bin/env sh
: "${1?❗Error❗: The Kafka listening port must be supplied (9092 for Docker Compose and 9093 for Testcontainers)}"

CONTAINER_KAFKA_ID=$(docker ps | grep 'confluentinc/cp-kafka*' | awk '{print $1}')

docker inspect --format='{{(index (index .NetworkSettings.Ports "'"${1}"'/tcp") 0).HostPort}}' $CONTAINER_KAFKA_ID