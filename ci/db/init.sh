#!/bin/sh
#---
# /docker-entrypoint-initdb.dにマウントすることで
# /tmp/init.dにマウントされた差分DDLをMySQLに投入します.
#
# /tmp/init.dにファイルが存在しない場合はエラーとなるため
# ファイルがある場合のみマウントしてください.
#---
ls -1 /tmp/init.d/*.sql | while read file
do
  mysql -uroot -proot < $file
done
