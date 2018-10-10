#!/usr/bin/env bash

set -e

HEROKU=registry.heroku.com

check_health() {
    echo checking health...
    until [[ $(curl --silent  --fail $1/mgmt/health/ 2>&1 | grep '"UP"') != "" ]]; do
        sleep 1
    done
    echo $1 is healthy!
}


if [ ${CIRCLE_BRANCH} == "master" ] || [ ${CIRCLE_BRANCH} == "develop" ]; then
    if [ "$CIRCLE_BRANCH" == "master" ]; then
        HEROKU_APP=ticktok-io-demo
    else
        HEROKU_APP=ticktok-io-dev
    fi
    IMAGE=$HEROKU/$HEROKU_APP/web
    echo $(heroku auth:token) | docker login --username=_ --password-stdin $HEROKU
    echo image: $IMAGE
    docker tag app $IMAGE
    docker push $IMAGE
    heroku --app ticktok-io-dev container:release web
    echo $IMAGE deployed to heroku
    check_health https://$HEROKU_APP.herokuapp.com
else
    echo No deployment from $CIRCLE_BRANCH branch
fi
