#!/usr/bin/env bash

# Using Ubuntu
curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash -

sudo systemctl stop apt-daily.service
sudo systemctl kill --kill-who=all apt-daily.service

# wait until `apt-get updated` has been killed
while ! (systemctl list-units --all apt-daily.service | fgrep -q dead)
do
  sleep 1;
done

sudo apt-get install -y nodejs
