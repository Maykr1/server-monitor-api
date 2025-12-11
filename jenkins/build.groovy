pipeline {
    // --- SETUP ---
    agent any
    options { timestamps() }
    tools { maven 'maven-3.9.11' }

    environment {
        // --- APP ---
        APP_NAME = "server-monitor-api"
        BUILD_TAG = "${env.BUILD_NUMBER}"

        // --- MAVEN ---
        NEXUS           = credentials('nexus-deploy')
        NEXUS_BASE      = "https://nexus.ethansclark.com"
        SNAPSHOT_REPO   = "${NEXUS_BASE}/repository/maven-snapshots/"

        // --- DOCKER ---
        DOCKER_BASE     = "localhost:8003"
    }

    stages {
        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B clean test'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests package'
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonar-local') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn -B -ntp sonar:sonar \
                                -Dsonar.projectKey=$APP_NAME \
                                -Dsonar.projectName="$APP_NAME" \
                                -Dsonar.token=$SONAR_TOKEN \
                            '''
                    }
                }

                timeout(time:10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Containerize') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-deploy', usernameVariable: 'DOCKER_USR', passwordVariable: 'DOCKER_PSW')]) {
                    sh '''
                        echo "[INFO] Deploying JAR to Nexus Maven..."
                        mvn -B -ntp -DskipTests deploy \
                            -DaltSnapshotDeploymentRepository=nexus-snapshots::default::$SNAPSHOT_REPO

                        echo "[INFO] Building Docker Image..."
                        echo "${DOCKER_PSW}" | docker login "${DOCKER_BASE}" -u "$DOCKER_USR" --password-stdin
                        
                        mvn -B -ntp \
                            -Ddocker.base="${DOCKER_BASE}" \
                            -Dapp.name="${APP_NAME}" \
                            -Dbuild.tag="${BUILD_TAG}" \
                            dockerfile:build dockerfile:tag dockerfile:push

                        docker logout "${DOCKER_BASE}"

                        echo "[INFO] Pushed image: ${DOCKER_BASE}/${APP_NAME}:${BUILD_TAG}"
                        '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build complete ✅' 
        }
        failure {
            echo 'Build failed ❌' 
        }
    }
}