pipeline {

    agent any

    tools{
        maven 'Maven 3.1.0'
    }

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
