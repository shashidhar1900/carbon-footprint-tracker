pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('shashi1900')
        DOCKERHUB_NAMESPACE   = 'shashi1900'
        IMAGE_TAG             = "${env.BUILD_NUMBER}"
        GATEWAY_BASE_URL      = 'http://34.14.173.231:8961'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }


        stage('Docker Build & Push') {
            steps {
                script {

                    def allServices = [
                        'service-registry',
                        'api-gateway',
                        'auth-service',
                        'transport-service',
                        'energy-service',
                        'food-service',
                        'analytics-service',
                        'leaderboard-service',
                        'frontend'
                    ]

                    sh '''
                        echo "$DOCKERHUB_CREDENTIALS_PSW" | \
                        docker login -u "$DOCKERHUB_CREDENTIALS_USR" --password-stdin
                    '''

                    allServices.each { svc ->

                        def image = "${DOCKERHUB_NAMESPACE}/carbon-footprint-tracker-${svc}"

                        def dockerContext =
                            svc == 'frontend'
                                ? "./frontend"
                                : "./backend/${svc}"

                        if (svc == 'frontend') {

                            sh """
                                docker buildx build \
                                    --load \
                                    --build-arg VITE_GATEWAY_BASE_URL=${GATEWAY_BASE_URL} \
                                    -t ${image}:${IMAGE_TAG} \
                                    -t ${image}:latest \
                                    ${dockerContext}

                                docker push ${image}:${IMAGE_TAG}
                                docker push ${image}:latest
                            """

                        } else {

                            sh """
                                docker buildx build \
                                    --load \
                                    -t ${image}:${IMAGE_TAG} \
                                    -t ${image}:latest \
                                    ${dockerContext}

                                docker push ${image}:${IMAGE_TAG}
                                docker push ${image}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['gcp-app-server']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no \
                            shashidharreddy0099@34.14.173.231 \
                            'cd ~/carbon-footprint-tracker && \
                            docker compose pull && \
                            docker compose up -d'
                    '''
                }
            }
        }
    }

    post {

        always {
            sh 'docker logout || true'
        }

        success {
            echo 'Pipeline succeeded — images built, pushed and deployed.'
        }

        failure {
            echo 'Pipeline failed — check the stage logs above.'
        }
    }
}
// "testing github webhook take3"
