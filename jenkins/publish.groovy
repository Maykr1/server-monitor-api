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
        NEXUS           = credentials('nexus-deploy')
        NEXUS_BASE      = "https://nexus.ethansclark.com"
        RELEASE_REPO_ID = "maven-releases"
        RELEASE_REPO    = "${NEXUS_BASE}/repository/${RELEASE_REPO_ID}/"

        // --- DOCKER ---
        DOCKER_BASE     = "localhost:8003"
    }

    parameters {
        choice(
            name: 'BUMP',
            choices: ['PATCH', 'MAJOR_CHANGE', 'APP_CHANGE'],
            description: 'Version bump for A.B.C: PATCH bumps C, MAJOR_CHANGE bumps B, APP_CHANGE bumps A'
        )
    }

    stages {
        stage ('Prepare Release') {
            steps {
                script {
                    if (! (env.BRANCH_NAME in ['main','master'])) {
                        error("This pipeline only publishes from main/master. Current branch: ${env.BRANCH_NAME}")
                    }
                }
            }
            
        }

        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Compute Release Version') {
            steps {
                script {
                    def xml = new XmlSlurper(false, false).parseText(readFile('pom.xml'))
                    def groupId = (xml.groupId?.text() ?: xml.parent.groupId.text()).trim()
                    def artifactId = xml.artifactId.text().trim()

                    echo "[INFO] Maven coordinates: ${groupId}:${artifactId}"

                    def latest = nexusLatestReleaseReversion(groupId, artifactId)
                    def next = bumpVersion(latest, params.BUMP)

                    env.RELEASE_VERSION = next
                    echo "[INFO] Latest release: ${latest ?: '(none)'} -> Next release: ${env.RELEASE_VERSION}"
                }
            }
        }

        stage('Set POM Version') {
            steps {
                sh """
                    mvn -B -q versions:set \
                      -DnewVersion=${RELEASE_VERSION} \
                      -DgenerateBackupPoms=false
                """
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

        stage('Containerize and Publish') {
            steps {
                containerizeApp('maven', APP_NAME, RELEASE_REPO, DOCKER_BASE, BUILD_TAG)
            }
        }
    }

    post {
        success {
            echo "Publish complete ✅ Released ${env.RELEASE_VERSION}" 
        }
        failure {
            echo 'Publish failed ❌' 
        }
    }
}