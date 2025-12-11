pipeline {
    agent any
    options { timestamps() }

    environment {
        // --- APP ---
        APP_NAME        = 'server-monitor-api'

        // --- DOCKER ---
        COMPOSE_DIR     = '/deploy'
        DOCKER_REG      = 'localhost:8003'
        DOCKER_REPO     = 'repository/docker-apps'
        IMAGE_TAG       = 'latest'
        IMAGE           = "${DOCKER_REG}/${APP_NAME}:${IMAGE_TAG}"
        REG_CRED_ID     = 'nexus-deploy'
        PRUNE_MODE      = "${params.PRUNE_MODE ?: 'none'}"
    }

    parameters{
        choice(name: 'PRUNE_MODE',
            choices: ['none', 'dangling', 'all'],
            description: 'Choose how aggressively to prune docker containers, volumes, etc.'
        )
    }

    stages {
        stage('Login to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.REG_CRED_ID, usernameVariable: 'DOCKER_USR', passwordVariable: 'DOCKER_PSW')]) {
                    sh '''
                        echo "[INFO] Logging into Docker registry ${DOCKER_REG}..."
                        echo "${DOCKER_PSW}" | docker login "${DOCKER_REG}" -u "${DOCKER_USR}" --password-stdin
                    '''
                }
            }
        }

        stage('Deploy latest image') {
            steps {
                sh '''
                    echo "[INFO] Deploying ${IMAGE}"

                    cd "${COMPOSE_DIR}"
                    TSP_TAG="${IMAGE_TAG}" docker compose pull ${APP_NAME}
                    TSP_TAG="${IMAGE_TAG}" docker compose up -d --no-build --remove-orphans ${APP_NAME}
                '''
            }
        }

        stage('Cleanup') {
            when { expression { env.PRUNE_MODE != 'none' } }
            steps {
                sh '''
                    echo "[INFO] Prune mode: ${PRUNE_MODE}"

                    if [ "${PRUNE_MODE}" = "all" ]; then
                        docker system prune -a
                    elif [ "${PRUNE_MODE}" = "dangling" ]; then
                        docker image prune -f
                        docker container prune -f
                        docker network prune -f
                    else 
                        echo "[INFO] Nothing to prune."
                    fi
                '''
            }
        }

        stage('Logout') {
            steps{
                sh '''
                    docker logout "${DOCKER_REG}" || true
                '''
            }
        }
        
    }

    post {
        success { 
            echo "✅ Successfully deployed latest ${APP_NAME} image" 
        }

        failure { 
            echo "❌ Deployment failed for ${APP_NAME}" 
        }
    }
}
