pipeline {

    agent any

    stages {
        stage("build") {
            steps {
                echo 'Building the application'
                sh 'mvn clean package'
            }
        }

        stage("code-analysis") {
            steps {
                echo 'Application code analysis'
                withSonarQubeEnv('sonarqube-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage("test") {
            steps {
                echo 'Testing the application'
            }
        }

        stage("deploy") {
            steps {
                echo 'Deploying the application'
            }
        }
    }
}
