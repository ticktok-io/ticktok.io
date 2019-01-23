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

if [[ "${CIRCLE_BRANCH}" == "develop" ]]; then
    HEROKU_APP=ticktok-io-dev
    heroku deploy:jar build/libs/*.jar --app $HEROKU_APP -o "--server.port=\$PORT"
    check_health https://$HEROKU_APP.herokuapp.com
else
    echo No deployment from $CIRCLE_BRANCH branch
fi
