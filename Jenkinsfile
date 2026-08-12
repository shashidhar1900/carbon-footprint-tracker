pipeline {
    agent any

    environment {
        // Jenkins credential of type "Username with password" — add this
        // in Jenkins UI: Manage Jenkins > Credentials, id must match exactly.
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        DOCKERHUB_NAMESPACE   = 'shashi1900' // change to your actual Docker Hub username
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
                        'service-registry', 'api-gateway', 'auth-service',
                        'transport-service', 'energy-service', 'food-service',
                        'analytics-service', 'leaderboard-service'
                    ]

                    def parallelStages = [:]

                    backendServices.each { svc ->
                        parallelStages["Build ${svc}"] = {
                            dir(svc) {
                                sh 'mvn -B clean package'
                            }
                        }
                    }

                    parallelStages['Build frontend'] = {
                        dir('frontend') {
                            sh 'npm ci'
                            sh 'npm run build'
                        }
                    }

                    parallel parallelStages
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def allServices = [
                        'service-registry', 'api-gateway', 'auth-service',
                        'transport-service', 'energy-service', 'food-service',
                        'analytics-service', 'leaderboard-service', 'frontend'
                    ]

                    sh """
                        echo \$DOCKERHUB_CREDENTIALS_PSW | docker login -u \$DOCKERHUB_CREDENTIALS_USR --password-stdin
                    """

                    allServices.each { svc ->
                        def image = "${DOCKERHUB_NAMESPACE}/carbontrail-${svc}"
                        sh """
                            docker build -t ${image}:${IMAGE_TAG} -t ${image}:latest ./${svc}
                            docker push ${image}:${IMAGE_TAG}
                            docker push ${image}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                // Same-host deploy: pulls the images just pushed above and
                // restarts the stack. If deploying to a SEPARATE server,
                // replace this with an sshagent block that runs the same
                // two commands remotely instead.
                sh """
                    DOCKERHUB_NAMESPACE=${DOCKERHUB_NAMESPACE} IMAGE_TAG=latest docker-compose -f docker-compose.yml pull
                    DOCKERHUB_NAMESPACE=${DOCKERHUB_NAMESPACE} IMAGE_TAG=latest docker-compose -f docker-compose.yml up -d
                """
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
        success {
            echo 'Pipeline succeeded — new images pushed and deployed.'
        }
        failure {
            echo 'Pipeline failed — check the stage logs above for the exact failing step.'
        }
    }
}
