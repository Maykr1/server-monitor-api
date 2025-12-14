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
                    env.SNAPSHOT_VERSION = getSnapshotVersion()
                }

                setVersion('maven', env.SNAPSHOT_VERSION)
                containerizeApp('maven', APP_NAME, SNAPSHOT_REPO, DOCKER_BASE, env.COMMIT_ID) // env.COMMIT_ID is set inside of getSnapshotVersion()
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
                    env.RELEASE_VERSION = getReleaseVersion('maven', params.BUMP)
                }

                setVersion('maven', env.RELEASE_VERSION)
                containerizeApp('maven', APP_NAME, RELEASE_REPO, DOCKER_BASE, env.RELEASE_VERSION)
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