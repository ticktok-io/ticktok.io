#!/usr/bin/env bash

set +e
curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.5/install.sh | bash
export NVM_DIR="/opt/circleci/.nvm"
[[ -s "$NVM_DIR/nvm.sh" ]] && \. "$NVM_DIR/nvm.sh"
nvm install v12.14.0
nvm alias default v12.14.0

# Each step uses the same `$BASH_ENV`, so need to modify it
echo 'export NVM_DIR="/opt/circleci/.nvm"' >> $BASH_ENV
echo "[ -s \"$NVM_DIR/nvm.sh\" ] && . \"$NVM_DIR/nvm.sh\"" >> $BASH_ENV
