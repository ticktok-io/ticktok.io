#!/usr/bin/env bash

docker-compose up -d
./gradlew test --tests e2e.*

