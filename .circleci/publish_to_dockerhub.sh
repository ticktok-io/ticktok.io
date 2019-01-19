#!/usr/bin/env bash

set -e

IMAGE_NAME=ticktok/ticktok

push_image() {
    docker tag $1 $2
    docker push $2
    echo $2 uploaded to Dockerhub
}

if [[ "${CIRCLE_BRANCH}" == "master" ]] || [[ "${CIRCLE_BRANCH}" == realease-* ]]; then
    TAG=`git describe --tags --abbrev=0`
    IMAGE=$IMAGE_NAME:$TAG
    SANDBOX=IMAGE-sandbox
    if [[ `docker pull $IMAGE` ]] || [[ `docker pull $SANDBOX` ]]; then
        echo $IMAGE already exists
        exit 1
    else
        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
        push_image app $IMAGE
        push_image sandbox $SANDBOX

        if [[ "${CIRCLE_BRANCH}" == "master" ]]; then
            push_image app $IMAGE_NAME:latest
            push_image sandbox $IMAGE_NAME:sandbox
        fi
    fi
else
    echo No publishing from $CIRCLE_BRANCH branch
fi
