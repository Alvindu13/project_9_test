pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                dir("src") {
                    sh "mvn -B -DskipTests clean package"
                }
            }
        }
        stage('Test') {
            steps {
                dir("src") {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    /*need update*/
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Deliver') {
            steps {
                dir("src") {
                    sh './jenkins/scripts/deliver.sh'
                }
            }
        }
    }
}
