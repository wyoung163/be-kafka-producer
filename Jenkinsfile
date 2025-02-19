pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/wyoung163/olive-young-be.git'
        GIT_CREDENTIALS = 'github-credential'
        AWS_REGION = 'ap-northeast-2'
        AWS_ACCOUNT_ID = '796973504685'
        ECR_REPO_NAME = 'server/kafka-producer'
        ECR_CREDENTIALS = 'aws-ecr-credential'
        SQ_CREDENTIALS = 'sonarqube-credential'
        SQ_PROJECT_KEY = 'sonarqube-project-key'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'dev', credentialsId: GIT_CREDENTIALS, url: REPO_URL
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'sonarqube-scanner';
                    withSonarQubeEnv(credentialsId: SQ_CREDENTIALS, installationName: 'sonarqube') {
                        withCredentials([string(credentialsId: SQ_PROJECT_KEY, variable: 'PROJECT_KEY')]) {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${PROJECT_KEY} \
                            -Dsonar.projectName="Your Project Name" \
                            -Dsonar.sources=src \
                            -Dsonar.sourceEncoding=UTF-8
                        """
                        }
                    }
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                script {
                    withAWS(credentials: ECR_CREDENTIALS, region: AWS_REGION) {
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def ecrImage = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:latest"
                    sh "docker build --cache-from=${ecrImage} -t ${ecrImage} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def ecrImage = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:latest"
                    sh "docker push ${ecrImage}"
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com'
        }
    }
}
