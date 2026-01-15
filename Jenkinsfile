pipeline {
    agent any
    
    triggers {
        GenericTrigger(
            genericVariables: [
                [key: 'WEBHOOK_REF', value: '$.ref'],
                [key: 'WEBHOOK_REPO', value: '$.repository.full_name'],
                [key: 'WEBHOOK_COMMIT', value: '$.after'],
                [key: 'WEBHOOK_PUSHER', value: '$.pusher.name'],
                [key: 'WEBHOOK_TAG', value: '$.ref', regexpFilter: 'refs/tags/'],
                [key: 'WEBHOOK_BRANCH', value: '$.ref', regexpFilter: 'refs/heads/']
            ],
            causeString: 'Triggered by webhook from $WEBHOOK_PUSHER',
            token: 'cercalia-sdk-java-webhook-token',
            printContributedVariables: true,
            printPostContent: true,
            regexpFilterText: '$WEBHOOK_REF',
            regexpFilterExpression: '^(refs/heads/.*|refs/tags/v.*)$'
        )
    }
    
    options {
        timestamps()
        disableConcurrentBuilds()
    }
    
    environment {
        CERCALIA_API_KEY = credentials('cercalia-api-key')
        GIT_TAG = "${env.WEBHOOK_TAG ?: ''}"
        GIT_BRANCH = "${env.WEBHOOK_BRANCH ?: ''}"
    }
    stages {
        stage('Build Matrix') {
            matrix {
                axes {
                    axis {
                        name 'JDK'
                        values 'temurin-8', 'temurin-11', 'temurin-17', 'temurin-21'
                    }
                }
                tools {
                    jdk "${JDK}"
                }
                stages {
                    stage('Tests') {
                        steps {
                            sh 'mvn -B -ntp clean verify'
                        }
                    }
                }
            }
        }
        stage('Release') {
            when {
                allOf {
                    expression {
                        // Comprueba si es un tag que empieza con 'v' desde el webhook
                        return (env.WEBHOOK_TAG ?: env.GIT_TAG ?: '').startsWith('v')
                    }
                }
            }
            tools {
                jdk 'temurin-8'
            }
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'cercalia-maven-central-portal', usernameVariable: 'CENTRAL_USERNAME', passwordVariable: 'CENTRAL_TOKEN'),
                    string(credentialsId: 'cercalia-gpg-private-key', variable: 'GPG_PRIVATE_KEY'),
                    string(credentialsId: 'cercalia-gpg-passphrase', variable: 'GPG_PASSPHRASE')
                ]) {
                    sh '''
                        set +x
                        echo "$GPG_PRIVATE_KEY" | gpg --batch --import
                    '''
                    sh 'mvn -B -ntp clean deploy -Dgpg.skip=false -Dgpg.passphrase="$GPG_PASSPHRASE"'
                }
            }
        }
    }
}
