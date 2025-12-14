@Library('shared-jenkins-library') _

pipeline {
    // --- SETUP ---
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { maven 'maven-3.9.11' }

    environment {
        // --- APP ---
        APP_NAME = "server-monitor-api"

        // --- MAVEN ---
        NEXUS               = credentials('nexus-deploy')
        NEXUS_BASE          = "https://nexus.ethansclark.com"
        SNAPSHOT_REPO_ID    = "maven-snapshots"
        SNAPSHOT_REPO       = "${NEXUS_BASE}/repository/${SNAPSHOT_REPO_ID}/"
        RELEASE_REPO_ID     = "maven-releases"
        RELEASE_REPO        = "${NEXUS_BASE}/repository/${RELEASE_REPO_ID}/"

        // --- DOCKER ---
        DOCKER_BASE     = "localhost:8003"
    }

    parameters {
        booleanParam(
            name: 'RELEASE', 
            defaultValue: false, 
            description: 'Publish Maven release (main only)'
        )
        choice(
            name: 'BUMP',
            choices: ['PATCH', 'MINOR', 'MAJOR'],
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

        stage('Publish Snapshot') {
            when { 
                allOf {
                    expression { !params.RELEASE }
                    not { changeRequest() }
                } 
            }
            steps {
                script {
                    def branch = env.BRANCH_NAME ?: sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    def sha    = (env.GIT_COMMIT ?: sh(script: "git rev-parse HEAD", returnStdout: true).trim()).take(7)

                    def safeBranch = branch.replaceAll(/[^0-9A-Za-z.\-]/, "-")

                    env.COMMIT_ID  = sha
                    env.SNAPSHOT_VERSION = "${safeBranch}-${sha}-SNAPSHOT"

                    echo "[INFO] Publishing snapshot version: ${env.SNAPSHOT_VERSION}"
                }

                setVersion('maven', SNAPSHOT_VERSION)
                containerizeApp('maven', APP_NAME, SNAPSHOT_REPO, DOCKER_BASE, COMMIT_ID)
            }
        }

        stage('Publish Release') {
            when {
                allOf {
                    branch 'main'
                    expression { params.RELEASE }
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

                    def latest = nexusLatestReleaseVersion(groupId, artifactId)
                    def next   = bumpVersion(latest, params.BUMP)

                    env.RELEASE_VERSION = next
                    echo "[INFO] Latest release: ${latest ?: '(none)'} -> Next release: ${env.RELEASE_VERSION}"
                }

                setVersion('maven', RELEASE_VERSION)
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