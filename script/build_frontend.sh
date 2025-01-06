set -o xtrace

./npm.sh ci
./npm.sh run 'build-watch'