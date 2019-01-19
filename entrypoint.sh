#!/usr/bin/env sh


port=${PORT:-8080}

exec java -Dserver.port=$port -Xmx32m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /opt/app/app.jar