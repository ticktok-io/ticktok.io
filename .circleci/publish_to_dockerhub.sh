#!/usr/bin/env bash

set -e

if [ "${CIRCLE_BRANCH}" == "master" ]; then
    TAG=`git describe --tags --abbrev=0`
    IMAGE=ticktok/ticktok:$TAG
    if [[ `docker pull $IMAGE` ]]; then
        echo $IMAGE already exists
        exit 1
    else
        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
        docker tag app $IMAGE
        docker push $IMAGE
    fi
fi
