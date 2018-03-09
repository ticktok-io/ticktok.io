#!/usr/bin/env bash

set -e

HEROKU=registry.heroku.com

if [ ${CIRCLE_BRANCH} == "master" ] || [ ${CIRCLE_BRANCH} == "develop" ]; then
    if [ "$CIRCLE_BRANCH" == "master" ]; then
        export TAG=$(git describe --tags --abbrev=0)
        IMAGE=$HEROKU/ticktok-io-demo/web:$TAG
    else
        export TAG=$(git rev-parse --short HEAD)
        IMAGE=$HEROKU/ticktok-io-dev/web:$TAG
    fi
    docker login --username=_ --password=$(heroku auth:token) $HEROKU
    echo image: $IMAGE
    docker tag app $IMAGE
    docker push $IMAGE
fi
