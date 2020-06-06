FROM alpine:3.12

VOLUME /tmp

# Install Java 11 & mongodb
RUN echo 'http://dl-cdn.alpinelinux.org/alpine/v3.6/main' >> /etc/apk/repositories
RUN echo 'http://dl-cdn.alpinelinux.org/alpine/v3.6/community' >> /etc/apk/repositories
RUN apk update && apk --no-cache --update add openjdk11 && apk --no-cache --update add mongodb

# Copy application files
ADD build/libs/ticktok-io.jar /opt/app/app.jar
ADD entrypoint.sh /opt/app
RUN chmod +x /opt/app/entrypoint.sh

WORKDIR /opt/app

EXPOSE 9643

CMD ["./entrypoint.sh"]
