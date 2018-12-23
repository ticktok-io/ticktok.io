#!/usr/bin/env bash


docker run -v `pwd`/dashboard:/opt/dashboard -w /opt/dashboard node:8-alpine npm "$@"