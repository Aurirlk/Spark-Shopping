pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "registry.cn-hangzhou.aliyuncs.com/shopping"
        APP_NAME = "shopping-backend"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Unit Test') {
            steps {
                dir('backend') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    docker.build("${DOCKER_REGISTRY}/${APP_NAME}:${BUILD_NUMBER}")
                    docker.build("${DOCKER_REGISTRY}/${APP_NAME}:latest")
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-credentials') {
                        docker.image("${DOCKER_REGISTRY}/${APP_NAME}:${BUILD_NUMBER}").push()
                        docker.image("${DOCKER_REGISTRY}/${APP_NAME}:latest").push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        docker-compose down
                        docker-compose pull
                        docker-compose up -d
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    timeout(time: 2, unit: 'MINUTES') {
                        sh 'for i in $(seq 1 12); do
                            curl -f http://localhost:8080/actuator/health && break
                            sleep 10
                        done'
                    }
                }
            }
        }
    }

    post {
        failure {
            emailext(
                subject: "构建失败: ${APP_NAME} #${BUILD_NUMBER}",
                body: "构建失败，请检查 Jenkins 构建日志。",
                to: "dev@shopping.com"
            )
        }
        success {
            emailext(
                subject: "部署成功: ${APP_NAME} #${BUILD_NUMBER}",
                body: "已成功部署到生产环境。",
                to: "dev@shopping.com"
            )
        }
    }
}
