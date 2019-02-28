#!/usr/bin/env sh


port=${PORT:-8080}

if [[ -z "${MONGO_URI}" ]]; then
    export MONGO_URI=mongodb://localhost/ticktok
    mkdir /tmp/mongo
    mongod --storageEngine ephemeralForTest --dbpath /tmp/mongo &
fi
exec java -Dserver.port=$port -Xmx64m -Xss512k -Djava.security.egd=file:/dev/./urandom -jar /opt/app/app.jar
