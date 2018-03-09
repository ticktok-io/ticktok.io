#!/usr/bin/env bash

set -e

HEROKU=registry.heroku.com

if [ ${CIRCLE_BRANCH} == "master" ] || [ ${CIRCLE_BRANCH} == "develop" ]; then
    if [ "$CIRCLE_BRANCH" == "master" ]; then
        IMAGE=$HEROKU/ticktok-io-demo/web
    else
        IMAGE=$HEROKU/ticktok-io-dev/web
    fi
    echo $(heroku auth:token) | docker login --username=_ --password-stdin $HEROKU
    echo image: $IMAGE
    docker tag app $IMAGE
    docker push $IMAGE
fi
