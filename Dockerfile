FROM openjdk:8-jdk-alpine

VOLUME /tmp

ADD build/libs/ticktok-0.1.0.jar /opt/app/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app/app.jar"]