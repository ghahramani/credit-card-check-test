#!/bin/bash

echo "================================== Waiting For Logstash ====================================="
$HOME/wait-for-it.sh $LOGSTASH_URI --strict --timeout=60 || { echo "Unable not connect to Logstash hosts"; exit 1; };
echo "================================= Logstash is accessible ===================================="

echo "================================== Waiting For Mongo ========================================"
$HOME/wait-for-it.sh $MONGODB_URI --strict --timeout=60 || { echo "Unable not connect to Mongo"; exit 1; };
echo "================================== Mongo is accessible ======================================"

echo "================================== Waiting For Elasticsearch ================================"
$HOME/wait-for-it.sh $ELASTICSEARCH_URI --strict --timeout=60 || { echo "Unable not connect to Elastisearch"; exit 1; };
echo "================================= Elasticsearch is accessible ==============================="

echo "================================== Waiting For Rabbitmq ====================================="
$HOME/wait-for-it.sh $RABBITMQ_URI --strict --timeout=60 || { echo "Unable not connect to RabbitMQ"; exit 1; };
echo "================================= Rabbitmq is accessible ===================================="

echo "================================== Waiting For Hazelcast ===================================="
$HOME/wait-for-it.sh $HAZELCAST_MANAGEMENT_URI --strict --timeout=60 || { echo "Unable not connect to Hazelcast"; exit 1; };
echo "================================= Hazelcast is accessible ==================================="

echo "=================================== Running Application ====================================="
exec honcho -f /Procfile start
echo "=================================== Application Has Stopped ====================================="
