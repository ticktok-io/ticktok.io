#!/usr/bin/env bash

set -e

IMAGE_NAME=ticktok/ticktok

push_image() {
    echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
    docker tag $1 $2
    docker push $2
    echo $2 uploaded to Dockerhub
}

publish_current_tag() {
    if [[ `docker pull $1` ]]; then
        echo $1 already exists
        exit 1
    else
        push_image app $1
    fi
}

if [[ $# -eq 0 ]]; then
    TAG=`git describe --tags --abbrev=0 | cut -d "v" -f 2`
    publish_current_tag $IMAGE_NAME:$TAG
elif [[ $1 == "master" ]]; then
    push_image app $IMAGE_NAME:master
else
    echo $1 tag isn\'t supported
fi
