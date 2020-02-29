#!/usr/bin/env bash

set -e

IMAGE_NAME=ticktok/ticktok

push_image() {
    docker tag $1 $2
    docker push $2
    echo $2 uploaded to Dockerhub
}

TAG=`git describe --tags --abbrev=0 | cut -d "v" -f 2`
IMAGE=$IMAGE_NAME:$TAG

if [[ `docker pull $IMAGE` ]]; then
    echo $IMAGE already exists
    exit 1
else
    echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
    push_image app $IMAGE

#    if [[ "${CIRCLE_BRANCH}" == "master" ]]; then
#        push_image app $IMAGE_NAME:latest
#    fi
fi
push_image app $IMAGE_NAME:master
