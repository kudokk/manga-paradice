_setup_for_container(){
  local PROXY_HOST="proxy-vip.cc1-6c1.internal"
  local PROXY_PORT="8080"

  export HTTP_PROXY="http://${PROXY_HOST}:${PROXY_PORT}"
  export http_proxy="http://${PROXY_HOST}:${PROXY_PORT}"
  export HTTPS_PROXY="http://${PROXY_HOST}:${PROXY_PORT}"
  export https_proxy="http://${PROXY_HOST}:${PROXY_PORT}"
  export NO_PROXY="maven-vip.cc1-6c1.internal"
  export no_proxy="maven-vip.cc1-6c1.internal"

  local mvn_proxy_opts=""
  mvn_proxy_opts="${mvn_proxy_opts} -Dhttp.proxyHost=${PROXY_HOST}"
  mvn_proxy_opts="${mvn_proxy_opts} -Dhttp.proxyPort=${PROXY_PORT}"
  mvn_proxy_opts="${mvn_proxy_opts} -Dhttps.proxyHost=${PROXY_HOST}"
  mvn_proxy_opts="${mvn_proxy_opts} -Dhttps.proxyPort=${PROXY_PORT}"
  mvn_proxy_opts="${mvn_proxy_opts} -Dhttp.nonProxyHosts=${NO_PROXY}"
  export MVN_PROXY_OPTS="$mvn_proxy_opts"
}

_echo_base_commit(){
  # 最新から n 個目
  # git log --pretty=format:"%H" | head -100 | tail -1

  # 直近のマージ
  # git log --pretty=format:"%H %s" | grep 'Merge pull request #' | head -1 | cut -d ' ' -f1
  ruby ci/git_utils.rb recent_merge_commit
}

_print_info(){
  set +o xtrace
  echo "----"
  num_lines=$(ruby ci/git_utils.rb num_lines_after_merge)
  echo "num_lines (${num_lines})"
  echo "----"
  echo "target commits:"
  git log --pretty=format:"%h %cd %s" --date=iso | head -n $num_lines
  echo "----"
  git log --decorate --all --oneline --graph | head -40
  echo "----"
  set -o xtrace
}

_print_target_files(){
  local ext="$1"; shift

  local base_commit=$(_echo_base_commit)

  git diff ${base_commit} -- \
    | egrep '\+ b/.+' | egrep '\.'${ext}'$' \
    | sed -e 's/^\+\+\+ b\///g'
}

_join_lines(){
  local result=""

  while read -r line
  do
    if [ "$result" == "" ]; then
      result="${line}"
    else
      result="${result} ${line}"
    fi
  done

  echo $result
}
