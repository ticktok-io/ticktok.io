#!/usr/bin/env bash

set -e

# export RABBITMQ_NODE_PORT=5789
export MONGO_URI=mongodb://localhost/ticktok
# export RABBIT_URI=amqp://localhost


# rabbitmq-server -detached

mkdir /tmp/mongo
mongod --storageEngine ephemeralForTest --dbpath /tmp/mongo &

