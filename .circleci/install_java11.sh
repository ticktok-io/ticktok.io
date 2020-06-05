#!/usr/bin/env bash

# sudo add-apt-repository ppa:linuxuprising/java
sudo apt update
sudo apt-get install openjdk-11-jdk
sudo update-java-alternatives --set /usr/lib/jvm/java-1.11.0-openjdk-amd64