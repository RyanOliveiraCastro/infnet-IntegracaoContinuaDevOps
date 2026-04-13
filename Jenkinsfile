pipeline {
    agent any

    environment {
        // Nome da imagem que vai para o Docker Hub
        IMAGE_NAME = 'ryanocastro/quarkus-app'
        IMAGE_TAG  = 'v2'
    }

    stages {
        stage('1. Build Quarkus (Java)') {
            steps {
                echo 'Compilando a aplicação...'
                sh './mvnw clean package'
            }
        }

        stage('2. Build e Push Docker (Docker Hub)') {
            steps {
                echo 'Gerando a imagem Docker...'
                sh "docker build -f src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME}:${IMAGE_TAG} ."

                // Nota: No Jenkins real, usaríamos credentials() para não expor a senha
                echo 'Enviando para o Docker Hub...'
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('3. Deploy no Kubernetes') {
            steps {
                echo 'Aplicando os manifestos no Cluster...'
                sh 'kubectl apply -f quarkus-app/postgres.yaml'
                sh 'kubectl apply -f quarkus-app/monitoring.yaml'
                sh 'kubectl apply -f quarkus-app/app-deployment.yaml'

                echo 'Reiniciando a aplicação para forçar a nova imagem...'
                sh 'kubectl rollout restart deployment quarkus-app-deploy'
            }
        }
    }
}