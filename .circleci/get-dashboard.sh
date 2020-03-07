#!/usr/bin/env bash

set -e

version=`cat dashboard-version.txt`
npm pack "ticktok-dashboard@$version"
tar zxvf "ticktok-dashboard-$version.tgz"
rm -Rf dashboard
mv package dashboard


