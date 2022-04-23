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
                sshPublisher(
                    continueOnError: false,
                    failOnError: true,
                    publishers: [
                        sshPublisherDesc(
                            configName: 'ansible-server',
                            transfers: [
                                sshTransfer(
                                    cleanRemote: false,
                                    excludes: '',
                                    execCommand: '',
                                    execTimeout: 120000,
                                    flatten: false,
                                    makeEmptyDirs: false,
                                    noDefaultExcludes: false,
                                    patternSeparator: '[, ]+',
                                    remoteDirectory: '.',
                                    remoteDirectorySDF: false,
                                    removePrefix: 'target',
                                    sourceFiles: 'target/*.jar'
                                ),
                                sshTransfer(sourceFiles: 'Dockerfile'),
                                sshTransfer(sourceFiles: 'graffitter-user-service.yml'),
                                sshTransfer(execCommand: 'ansible-playbook graffitter-user-service.yml')
                            ],
                            usePromotionTimestamp: false,
                            useWorkspaceInPromotion: false,
                            verbose: true
                        )
                    ]
                )
            }
        }
    }
}
