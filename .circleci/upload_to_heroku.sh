#!/usr/bin/env bash

if [ "${CIRCLE_BRANCH}" == "master" ] || [ "${CIRCLE_BRANCH}" == "develop" ]; then
    if [ "${CIRCLE_BRANCH}" == "master" ]; then
        export TAG=`git describe --tags --abbrev=0`
    else
        export TAG=`git rev-parse --short HEAD`
    fi
    docker tag app $IMAGE:$TAG
    docker login --username=$HEROKU_USERNAME --password=$HEROKU_PASSWORD registry.heroku.com
    docker push registry.heroku.com/$IMAGE:$TAG/web
fi
