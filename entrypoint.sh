#!/usr/bin/env sh


port=${PORT:-8080}

[[ -z "$MONGO_URI" ]] && { export MONGO_URI=mongodb://localhost/ticktok; mkdir /tmp/mongo; mongod --storageEngine ephemeralForTest --dbpath /tmp/mongo & }
exec java -Dserver.port=$port -Xmx32m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /opt/app/app.jar