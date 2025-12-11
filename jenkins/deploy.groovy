@Library('shared-jenkins-library')

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
                login(REG_CRED_ID, DOCKER_REG)
            }
        }

        stage('Deploy latest image') {
            steps {
                deploy(IMAGE, COMPOSE_DIR, IMAGE_TAG, APP_NAME)
            }
        }

        stage('Cleanup') {
            when { expression { env.PRUNE_MODE != 'none' } }
            cleanupServer(PRUNE_MODE)
        }

        stage('Logout') {
            logout(DOCKER_REG)
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
