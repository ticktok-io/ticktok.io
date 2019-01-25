#!/usr/bin/env bash

docker-compose -f docker-compose.yml -f .circleci/docker-compose-sandbox.ci.yml "$@"
