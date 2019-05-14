FROM openjdk:8-jdk-alpine

VOLUME /tmp

RUN apk add mongodb

ADD build/libs/ticktok-*.jar /opt/app/app.jar
ADD entrypoint.sh /opt/app
RUN chmod +x /opt/app/entrypoint.sh

WORKDIR /opt/app

EXPOSE 9643

CMD ["./entrypoint.sh"]
