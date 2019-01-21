#!/usr/bin/env bash

set -e

export MONGO_URI=mongodb://localhost/ticktok
export RABBIT_URI=amqp://localhost:5789


# rabbitmq-server -detached

mkdir /tmp/mongo
mongod --storageEngine ephemeralForTest --dbpath /tmp/mongo &

