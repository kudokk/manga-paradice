set -o xtrace

# マウントしたディレクトリから必要なファイルだけをコンテナ内にコピー
mkdir -p /tmp/work
mkdir -p /tmp/work/src/main
mkdir -p /tmp/work/src/test
cp -rp /tmp/project/src/main/kotlin /tmp/work/src/main/
cp -rp /tmp/project/src/test/kotlin /tmp/work/src/test/
mkdir -p /tmp/work/ci
cp -p /tmp/project/ci/common.sh /tmp/work/ci/
cp -p /tmp/project/ci/git_utils.rb /tmp/work/ci/
cp -p /tmp/project/ci/lint/.editorconfig /tmp/work/
cp -rp /tmp/project/.git /tmp/work/

cd /tmp/work

source ./ci/common.sh
_setup_for_container

# この設定がないと「fatal: detected dubious ownership in repository」といエラーが発生して失敗する
git config --global --add safe.directory /tmp/work

_print_info

# ファイル指定
_echo_base_commit
set +o xtrace
target_files="$(_print_target_files 'kt' | _join_lines)"
set -o xtrace
echo "target_files (${target_files})"

cs_out=/tmp/checkstyle.out
if [ "$target_files" != "" ]; then
  ktlint --editorconfig=/tmp/work/.editorconfig $target_files > $cs_out
else
  exit 0
fi

# shellcheck disable=SC2002
cnt=$(cat $cs_out | wc -l)
if [ $cnt -ge 2 ]; then
  set +o xtrace
  echo "================================================================"
  echo ""

  cat $cs_out

  echo ""
  echo "================================================================"
  set -o xtrace
  exit 1
fi
