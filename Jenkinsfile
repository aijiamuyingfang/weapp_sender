pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '25'))
        disableConcurrentBuilds()
        timestamps()
        skipDefaultCheckout()
    }

    stages {
        
        stage('get code') {
            steps {
                git 'https://github.com/fuyoushengwu/weapp_sender.git'
            }
        }
    }

    post {

        success {
            dir("sources") {
                archive 'target/*.jar'
            }
        }
    }

}