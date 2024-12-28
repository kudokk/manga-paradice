# syntax=docker/dockerfile:experimental
FROM --platform=linux/amd64 openjdk:17 AS base
RUN mkdir /app
WORKDIR /app
COPY ./mvnw /app
COPY ./.mvn /app/.mvn
COPY ./pom.xml /app
COPY ./owasp-dependency-check-suppressions.xml /app

FROM base as build
COPY ./src /app/src
ARG PROFILE=unknown
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw install -Dmaven.test.skip=true -P$PROFILE

FROM base as run
COPY --from=build /app/target/compass-manager-0.0.1-SNAPSHOT.war .
CMD ["java", "-jar", "compass-manager-0.0.1-SNAPSHOT.war"]

ARG GIT_REVISION=unknown
ARG GIT_ORIGIN=unknown
ARG IMAGE_NAME=unknown
LABEL git-revision=$GIT_REVISION \
      git-origin=$GIT_ORIGIN \
      image-name=$IMAGE_NAME
