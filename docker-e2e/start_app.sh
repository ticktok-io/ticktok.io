#!/usr/bin/env bash

docker run \
    - RABBIT_URI=$RABBIT_URI
    - MONGO_URI=$MONGO_URI
    - SELF_DOMAIN=http://localhost:8081
    - ACCESS_TOKEN=$E2E_ACCESS_TOKEN
    -p 8081:8080 \
    -v ./application.yml:/opt/app/config/application.yml \
    -d \
    app