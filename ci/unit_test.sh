set -o xtrace
set -o pipefail

# マウントしたディレクトリから必要なファイルだけをコンテナ内にコピー
mkdir -p /tmp/work
mkdir -p /tmp/work/src/main
mkdir -p /tmp/work/src/test
cp -rp /tmp/project/src/main/kotlin /tmp/work/src/main/
cp -rp /tmp/project/src/main/resources /tmp/work/src/main/
cp -rp /tmp/project/src/test/kotlin /tmp/work/src/test/
cp -rp /tmp/project/src/test/resources /tmp/work/src/test/
cp -p /tmp/project/pom.xml /tmp/work/
cp -p /tmp/project/mvnw /tmp/work/
cp -rp /tmp/project/.mvn /tmp/work/
cp -rp /tmp/project/.git /tmp/work/

mkdir -p /tmp/work/ci
cp -p /tmp/project/ci/common.sh /tmp/work/ci/

cd /tmp/work

source ./ci/common.sh
_setup_for_container

update-java-alternatives --list
java -version
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64

./mvnw --batch-mode ${MVN_PROXY_OPTS} test \
  | grep -v -e '^\[INFO\] Downloading from ' \
  | grep -v -e '^\[INFO\] Downloaded from '

if [ $? -ne 0 ]; then
  errs="${errs},test_js"
fi

if [ "$errs" != "" ]; then
  echo "errors (${errs})"
  exit 1
else
  exit 0
fi
