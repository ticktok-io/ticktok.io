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

#    IMAGE=$HEROKU/$HEROKU_APP/web
#    echo $(heroku auth:token) | docker login --username=_ --password-stdin $HEROKU
#    echo image: $IMAGE
#    docker tag app $IMAGE
#    docker push $IMAGE
#    heroku container:release web --app ticktok-io-dev
#    echo $IMAGE deployed to heroku

    heroku git:remote -a $HEROKU_APP
    git push heroku master

    check_health https://$HEROKU_APP.herokuapp.com
else
    echo No deployment from $CIRCLE_BRANCH branch
fi
