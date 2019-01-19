FROM openjdk:8-jdk-alpine

VOLUME /tmp

RUN apk update
RUN apk add mongodb
RUN apk add rabbitmq

ADD build/libs/ticktok-*.jar /opt/app/app.jar
ADD entrypoint.sh /opt/app
RUN chmod +x /opt/app/entrypoint.sh

WORKDIR /opt/app

CMD ["./entrypoint.sh"]