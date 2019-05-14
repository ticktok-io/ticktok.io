#!/usr/bin/env sh


port=${PORT:-6943}

if [[ -z "${MONGO_URI}" ]]; then
    export MONGO_URI=mongodb://localhost/ticktok
    mkdir /tmp/mongo
    mongod --storageEngine ephemeralForTest --dbpath /tmp/mongo &
fi
if [[ -z "${RABBIT_URI}" ]]; then
    export SPRING_PROFILES_ACTIVE=http
else
    export MANAGEMENT_HEALTH_RABBIT_ENABLED=true
fi
exec java -Dserver.port=$port -Xmx64m -Xss512k -Djava.security.egd=file:/dev/./urandom -jar /opt/app/app.jar
