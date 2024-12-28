pipeline {
    agent {
        label 'worker'
    }
    environment {
      TIMESTAMP="""${sh(returnStdout: true, script: 'date +%s | tr -d \'\n\'')}"""
    }
    stages {
        stage('CI Docker Image build') {
            // Dockerfileに変更がある場合のみこのstageが実行される
            when {
                changeset "ci/Dockerfile"
            }
            steps {
                sh "make docker-build"
                sh "make docker-push"
            }
        }
        stage('Test') {
            environment {
                MAVEN_LOCAL_REPOSITORY_DIR='/var/tmp/caches/compass-manager/.m2'
            }
            steps {
                 sh 'printenv'
                 sh "make docker-maven-start"
                 sh "make unit-test"
                 sh "make ktlint"
            }
            post {
                always {
                     sh 'make docker-maven-stop'
                }
            }
        }
       stage('Docker build (devel)') {
            steps {
                sh 'make devel_build'
                sh 'make devel_push'
                sh 'make devel_clean'
            }
        }
       stage('Docker build (prod)') {
           when {
               branch 'release'
           }
           steps {
               sh 'make prod_build'
               sh 'make prod_push'
               sh 'make prod_clean'
           }
       }
    }
}
