#!/usr/bin/env bash

docker run \
    -e RABBIT_URI=$RABBIT_URI \
    -e MONGO_URI=$MONGO_URI \
    -e SELF_DOMAIN=http://localhost:8081 \
    -e ACCESS_TOKEN=$E2E_ACCESS_TOKEN \
    -p 8081:8080 \
    -v `pwd`/docker-e2e/application.yml:/opt/app/config/application.yml \
    app