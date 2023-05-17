pipeline {
    agent {
        node {
            label 'master'
        }
    }
    environment {
        SONARQUBE_NAME = "SonarQube"                 // Find name in Manage Jenkins > Configure System > SonarQube Servers
        SQ_SCANNER_HOME = tool 'SonarQube Scanner'   // Find tool name in Manage Jenkins > Global Tool Configuration > SonarQube Scanner
        PROJECT = "Test Project"                 
    }
    stages {
        stage ('Repo checkout') {   // Delete if checkout is automatical 
            <checkout command>
        }
        stage ('Java compilation') {   // Java only, delete if no java code in repo. Compiled java code is needed for SonarQube analysis.
            <compilation command>
        }
        stage('SonarQube branch analysis') {
            when {
                not {changeRequest()}
            }
            steps {
                withSonarQubeEnv(SONARQUBE_NAME) {
                    sh """
                        ${SQ_SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.sources=. \
                            -Dsonar.projectKey=${PROJECT} \
                            -Dsonar.projectVersion=${env.BUILD_NUMBER} \
                            -Dsonar.branch.name=${GIT_BRANCH}   // Use this property if you are using sonarqube-community-branch-plugin
                    """
                }
            }
        }
        stage('SonarQube pull request analysis') {   // Use this stage if you are using sonarqube-community-branch-plugin
            when {
                changeRequest()
            }
            steps {
                withSonarQubeEnv(SONARQUBE_NAME) {
                    sh """
                        ${SQ_SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.sources=. \
                            -Dsonar.projectKey=${PROJECT} \
                            -Dsonar.pullrequest.branch=${CHANGE_BRANCH} \
                            -Dsonar.pullrequest.key=${CHANGE_ID} \
                            -Dsonar.pullrequest.base=${CHANGE_TARGET}
                    """
                }
            }
        }
        stage('SonarQube quality gate') {   // Catches webhook from SonarQube, use this stage if you are not using build-breaker plugin
            agent none
            steps {
                sleep 300
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        <other stages>
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}
