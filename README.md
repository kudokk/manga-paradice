# compass-manager
## バージョン (2023/09/27時点)
- Kotlin: 1.9.10

## ディレクトリ構成
```
.
├── ci # ユニットテスト/CI用のDockerDBの差分DDL
│   └── db
│       ├── [DBごとの差分DDLのディレクトリ]
│       └── init.sh
├── docker
│   └── docker-compose.yml # セッションRedisなど開発に必要なコンテナの定義
├── src/main/kotlin/jp/mangaka/ssp
│   ├── application # アプリケーション層
│   │   ├── service
│   │   └── valueobject
│   ├── infrastructure # インフラストラクチャ層
│   │   ├── config
│   │   └── dao
│   ├── presentation # プレゼンテーション層
│   │   ├── config
│   │   └── controller
│   └── util # 3層に含まれない共通処理など
├── src/main/resources
│   ├── messages # メッセージ関連
│   ├── templates # thymeleaf関連
│   └── application.yml
├── src/test/kotlin # ユニットテスト
├── src/test/resources
│   ├── dataset # Daoのテストデータ
│   └── application.yml
├── mvnw
├── owasp-dependency-check-suppressions.xml
├── pom.xml
└── README.md
```

## アプリケーション起動
### アプリケーションの立ち上げ
```
./mvnw spring-boot:run
```

### ローカル環境用DB
https://octocat.mangaka.co.jp/compass-web/compass_specs

### docker-composeの起動/停止
spring-session-data-redis 用の Redis
```
## 起動
docker-compose -f docker/docker-compose.yml up -d
## 稼働確認
docker-compose -f docker/docker-compose.yml ps
## 停止
docker-compose -f docker/docker-compose.yml down -v
```

### 開発時のフロントとの接続方法
targetファイル内のwarファイルからdocker imageを作成
```
## warファイルの作成
./mvnw package spring-boot:repackage -DskipTests
## docker imageのビルド
make local_build
```
imageファイルの起動はフロント側(compass-manager-ui)で行う

## Javaのテスト
### ユニットテスト用DBの起動/停止
ユニットテスト/CI用の MySQL は [docker-maven-plugin](https://dmp.fabric8.io/) で管理しています。
Daoを含むテストを実行する場合は、事前にコンテナを立ち上げてください。

```
## 起動
./mvnw docker:start

## 停止
./mvnw docker:stop
```

### テスト実行
```
./mvnw test
```

## その他
### Java のライブラリ脆弱性チェック
[参考記事](https://qiita.com/wrongwrong/items/aa64354379eba7f83e40)
```
./mvn dependency-check:check
```
