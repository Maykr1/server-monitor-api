@Library('shared-jenkins-library') _

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
        NEXUS               = credentials('nexus-deploy')
        NEXUS_BASE          = "https://nexus.ethansclark.com"
        SNAPSHOT_REPO_ID    = "maven-snapshots"
        SNAPSHOT_REPO       = "${NEXUS_BASE}/repository/${SNAPSHOT_REPO_ID}/"
        RELEASE_REPO_ID = "maven-releases"
        RELEASE_REPO    = "${NEXUS_BASE}/repository/${RELEASE_REPO_ID}/"

        // --- DOCKER ---
        DOCKER_BASE     = "localhost:8003"
    }

    parameters {
        choice(
            name: 'BUMP VERSION',
            choices: ['PATCH', 'MINOR', 'MAJOR'],\
            description: 'Release bump A.B.C: PATCH->C, MAJOR_CHANGE->B, APP_CHANGE->A'
        )
    }

    stages {
        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                testApp('maven')
            }
        }

        stage('Build') {
            steps {
                buildApp('maven')
            }
        }

        stage('SonarQube') {
            steps {
                sonarApp('maven', APP_NAME)
            }
        }

        stage('Containerize') {
            steps {
                containerizeApp('maven', APP_NAME, SNAPSHOT_REPO, DOCKER_BASE, BUILD_TAG)
            }
        }

        stage('Publish Release') {
            when {
                allOf {
                    branch 'main'
                    not { changeRequest() }
                }
            }

            steps {
                script {
                    def groupId = sh(
                        script: "mvn -q -DforceStdout help:evaluate -Dexpression=project.groupId",
                        returnStdout: true
                    ).trim()

                    def artifactId = sh(
                        script: "mvn -q -DforceStdout help:evaluate -Dexpression=project.artifactId",
                        returnStdout: true
                    ).trim()

                    echo "[INFO] Maven coordinates: ${groupId}:${artifactId}"

                    // - nexusLatestReleaseVersion(groupId, artifactId) -> returns latest or null
                    // - bumpVersion(latest, bump) -> returns 1.0.0 if latest is null
                    def latest = nexusLatestReleaseVersion(groupId, artifactId)
                    def next   = bumpVersion(latest, params.BUMP)

                    env.RELEASE_VERSION = next
                    echo "[INFO] Latest release: ${latest ?: '(none)'} -> Next release: ${env.RELEASE_VERSION}"
                }

                sh """
                    mvn -B -q versions:set \
                        -DnewVersion=${RELEASE_VERSION} \
                        -DgenerateBackupPoms=false
                    """

                // Publish to releases
                containerizeApp('maven', APP_NAME, RELEASE_REPO, DOCKER_BASE, RELEASE_VERSION)
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