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
        RELEASE_REPO    = "${NEXUS_BASE}/repository/maven-releases/"
        SNAPSHOT_REPO   = "${NEXUS_BASE}/repository/maven-snapshots/"

        // --- DOCKER ---
        DOCKER_BASE     = "localhost:8003"
    }

    stages {
        stage('Checkout Repo') {
            steps {
                githubNotify context: 'build', status: 'PENDING'
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
                            -DaltReleaseDeploymentRepository=nexus-releases::default::$RELEASE_REPO \
                            -DaltSnapshotDeploymentRepository=nexus-snapshots::default::$SNAPSHOT_REPO

                        echo "[INFO] Building Docker Image..."
                        IMAGE="${DOCKER_BASE}/$APP_NAME:$BUILD_TAG"

                        docker build \
                            --build-arg JAR_FILE=target/ToDoList2-0.0.1-SNAPSHOT.jar \
                            -t "$IMAGE" .
                        docker tag "$IMAGE" "${DOCKER_BASE}/${APP_NAME}:latest"

                        echo "[INFO] Pushing Docker image to Nexus Docker..."
                        echo "${DOCKER_PSW}" | docker login "${DOCKER_BASE}" -u "$DOCKER_USR" --password-stdin
                        docker push "$IMAGE"
                        docker push "${DOCKER_BASE}/$APP_NAME:latest"
                        docker logout "${DOCKER_BASE}"

                        echo "[INFO] Pushed: ${IMAGE}"
                        '''
                }
            }
        }
    }

    post {
        success {
            githubNotify context: 'build', status: 'SUCCESS'
            echo 'Build complete ✅' 
        }
        failure {
            githubNotify context: 'build', status: 'FAILURE'
            echo 'Build failed ❌' 
        }
    }
}