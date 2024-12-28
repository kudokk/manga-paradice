# CI

## イメージのビルド・プッシュ
レジストリの権限により`CI Jenkins`からのみpushできるため、PRで`Dockerfile`に差分がある場合のみ build&push が行われる。  
（`Jenkinsfile`の設定を参照）

`Dockerfile`を更新する場合は`ci/run.sh`の`TAG`も変更すること。
```
make docker-build
make docker-push
```

## CI用DockerDBの起動と停止

```
make docker-maven-start
make docker-maven-stop
```

## ユニットテスト実行

```
make unit-test
```

## lintの実行
ベースブランチと差分があるクラスのみがチェック対象  
lintの設定は`ci/lint/.editorconfig`を参照
```
make ktlint
```
