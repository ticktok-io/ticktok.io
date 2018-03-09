FROM openjdk:8-jdk-alpine

VOLUME /tmp

ADD build/libs/ticktok-0.1.0.jar /opt/app/app.jar
ADD entrypoint.sh /opt/app
RUN chmod +x /opt/app/entrypoint.sh

CMD ["/opt/app/entrypoint.sh"]