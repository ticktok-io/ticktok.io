#!/usr/bin/env bash

echo checking health...
until [[ $(curl --silent  --fail http://aaaa/mgmt/health/ 2>&1 | grep '"UP"') != "" ]]; do
    sleep 1
done
echo ticktok.io is healthy!