pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('shashi1900')
        DOCKERHUB_NAMESPACE   = 'shashi1900'
        IMAGE_TAG             = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {

                    def backendServices = [
                        'service-registry',
                        'api-gateway',
                        'auth-service',
                        'transport-service',
                        'energy-service',
                        'food-service',
                        'analytics-service',
                        'leaderboard-service'
                    ]

                    // Build backend services sequentially
                    backendServices.each { svc ->
                        stage("Build ${svc}") {
                            dir("backend/${svc}") {
                                sh 'mvn -B clean package'
                            }
                        }
                    }

                    // Build frontend
                    stage('Build frontend') {
                        dir('frontend') {
                            sh 'npm ci'
                            sh 'npm run build'
                        }
                    }
                }
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

                        def image =
                            "${DOCKERHUB_NAMESPACE}/carbon-footprint-tracker-${svc}"

                        def dockerContext =
                            svc == 'frontend'
                                ? "./frontend"
                                : "./backend/${svc}"

                        sh """
                            docker build \
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

        stage('Deploy') {
            steps {
                sh '''
                    docker compose pull
                    docker compose up -d
                '''
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