#!/usr/bin/env sh


port=${PORT:-8080}

if [[ ! -v MONGO_URI ]]; then
    mongod --storageEngine inMemory --dbpath /tmp/mongo &
fi
exec mongod java -Dserver.port=$port -Xmx32m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /opt/app/app.jar