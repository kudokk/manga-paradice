#!/bin/bash

REGISTRY=registry.dc.mangaka.jp
PROJECT=compass_manager_ci
TAG=0.0.1

# ユーザと同じ
NAMESPACE=compass-web

IMAGE_NAME_FULL=${REGISTRY}/${NAMESPACE}/${PROJECT}

if [ -z "${MAVEN_LOCAL_REPOSITORY_DIR}" ]; then
  export MAVEN_LOCAL_REPOSITORY_DIR=$HOME/.m2
fi

DOCKER_NETWORK="compass-manager-ci-network-$TIMESTAMP"
DOCKER_MYSQL_PORT=3306
DOCKER_COMPASS_MASTER_DB_NAME="compass-master-db-$TIMESTAMP"
DOCKER_COMPASS_LOG_DB_NAME="compass-log-db-$TIMESTAMP"
DOCKER_COMPASS_SUMMARY_DB_NAME="compass-summary-db-$TIMESTAMP"

MAVEN_PARAMS="-Ddocker.mysql.network.name=${DOCKER_NETWORK} \
  -Ddocker.mysql.compass-master-db.name=${DOCKER_COMPASS_MASTER_DB_NAME} \
  -Ddocker.mysql.compass-master-db.port=${DOCKER_MYSQL_PORT} \
  -Ddocker.mysql.compass-log-db.name=${DOCKER_COMPASS_LOG_DB_NAME} \
  -Ddocker.mysql.compass-log-db.port=${DOCKER_MYSQL_PORT} \
  -Ddocker.mysql.compass-summary-db.name=${DOCKER_COMPASS_SUMMARY_DB_NAME} \
  -Ddocker.mysql.compass-summary-db.port=${DOCKER_MYSQL_PORT}"

_docker_build(){
  cd ci/
  docker build -t ${IMAGE_NAME_FULL}:${TAG} ./
}

_docker_push(){
  docker push ${IMAGE_NAME_FULL}:${TAG}
}

_docker_run_common(){
  docker run --rm -v "$(pwd):/tmp/project" \
    -e GIT_BRANCH=${GIT_BRANCH} \
    ${IMAGE_NAME_FULL}:${TAG} "$@"
}

_docker_run_with_db(){
  docker run --rm -v "$(pwd):/tmp/project" -v "${MAVEN_LOCAL_REPOSITORY_DIR}:/root/.m2" --network ${DOCKER_NETWORK} \
    -e GIT_BRANCH=${GIT_BRANCH} \
    -e DOCKER_COMPASS_MASTER_DB="${DOCKER_COMPASS_MASTER_DB_NAME}:${DOCKER_MYSQL_PORT}" \
    -e DOCKER_COMPASS_LOG_DB="${DOCKER_COMPASS_LOG_DB_NAME}:${DOCKER_MYSQL_PORT}" \
    -e DOCKER_COMPASS_SUMMARY_DB="${DOCKER_COMPASS_SUMMARY_DB_NAME}:${DOCKER_MYSQL_PORT}" \
    ${IMAGE_NAME_FULL}:${TAG} "$@"
}

# 動作確認用
_docker_run(){
  docker run --rm -it -v "$(pwd):/tmp/project" \
    -e GIT_BRANCH=${GIT_BRANCH} \
    ${IMAGE_NAME_FULL}:${TAG} "$@"
}

_docker_maven_start(){
  ./mvnw --batch-mode docker:start "$@" \
    | grep -v -e '^\[INFO\] Downloading from ' \
    | grep -v -e '^\[INFO\] Downloaded from '
}

_docker_maven_stop(){
  ./mvnw --batch-mode docker:stop "$@" \
    | grep -v -e '^\[INFO\] Downloading from ' \
    | grep -v -e '^\[INFO\] Downloaded from '
}

_unit_test(){
  _docker_run_with_db /bin/bash /tmp/project/ci/unit_test.sh
}

_ktlint(){
 _docker_run_common /bin/bash /tmp/project/ci/ktlint.sh
}

cmd="$1"; shift

case $cmd in
  docker-build)
    _docker_build
    ;;
  docker-push)
    _docker_push
    ;;
  docker-run)
    _docker_run
    ;;
  docker-maven-start)
    _docker_maven_start $MAVEN_PARAMS
    ;;
  docker-maven-stop)
    _docker_maven_stop $MAVEN_PARAMS
    ;;
  unit-test)
    _unit_test
    ;;
  ktlint)
    _ktlint
    ;;
  *)
    echo "invalid command (${cmd})"
esac
