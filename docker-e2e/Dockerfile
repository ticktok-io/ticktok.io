FROM gradle:jdk8-alpine


COPY docker-e2e/ /opt/e2e/
COPY src/test/kotlin/ /opt/e2e/src/test/kotlin/
ADD build/libs/ticktok.io-tests.jar /opt/e2e/

WORKDIR /opt/e2e

USER root

RUN gradle dependencies

CMD ["gradle", "test"]