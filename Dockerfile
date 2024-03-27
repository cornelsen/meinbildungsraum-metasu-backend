FROM openjdk:19-jdk-oraclelinux8


ARG APP_VERSION=1.0
ARG BUILD_NR
ARG BUILD_TIMESTAMP
ARG BRANCH

LABEL appName=metasu-app-backend

LABEL branch=$BRANCH
LABEL buildNr=$BUILD_NR
LABEL appVersion=$APP_VERSION
LABEL buildTimestamp=$BUILD_TIMESTAMP

ENV INFO_BUILD-NR $BUILD_NR
ENV INFO_BUILD-TIMESTAMP $BUILD_TIMESTAMP
ENV TZ=Europe/Berlin

COPY target/metasu-app-backend-*.jar metasu-app-backend.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar metasu-app-backend.jar"]
