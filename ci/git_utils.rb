# coding: utf-8

Encoding.default_external = "UTF-8"

# 直近の何個分のマージまでを対象にするか
RECENT_MERGE_COUNT = 1

# - Jenkins CI による実行の場合 true
# - それ以外（ローカルでの実行）の場合 false
def is_jenkins_ci
  # 暫定
  ENV.key?("GIT_BRANCH") && /^PR-/ =~ ENV["GIT_BRANCH"]
end

def get_pr_branch_head
  if is_jenkins_ci()
    # Jenkins CI
    git_branch = ENV["GIT_BRANCH"]
    out = `git log -n 1 --pretty=format:"%H" origin/#{git_branch}`
  else
    # ローカル
    out = `git log -n 1 --pretty=format:"%H"`
  end

  out.strip
end

def _recent_merge(n)
  pr_branch_head = get_pr_branch_head()
  out = `git log -n 1000 --pretty=format:"%H %p" #{pr_branch_head}`

  merge_cnt = 0

  lineno = 0
  hash = nil
  out.each_line do |line|
    lineno += 1
    line.chomp!
    cols = line.split(" ")
    hash = cols[0]
    if cols.size >= 3
      merge_cnt += 1
      break if merge_cnt >= n
    end
  end

  [lineno, hash]
end

def recent_merge_commit
  _, hash = _recent_merge(RECENT_MERGE_COUNT)
  puts hash
end

def num_lines_after_merge
  lineno, _ = _recent_merge(RECENT_MERGE_COUNT)
  puts lineno - 1
end

case ARGV[0]
when "recent_merge_commit"
  recent_merge_commit
when "num_lines_after_merge"
  num_lines_after_merge
else
  raise "Invalid argument"
end
