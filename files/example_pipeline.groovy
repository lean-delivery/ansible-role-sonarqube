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
        stage('SonarQube pull request analysis - Bitbucket Server') {   // Obsolete, use this stage if you are using sonar-stash-plugin and SonarQube 7.6 (and less)
            when {
                changeRequest()
            }
            environment {
                BITBUCKET_URL = "<set>"
                BITBUCKET_PROJECT = "<set>"
                BITBUCKET_REPO = "<set>"
                BITBUCKET_CREDS = credentials('<jenkins credentials id for Bitbucket Server')
                COMMENT_SEVERITY = "MAJOR"
                APPROVAL_SEVERITY = "MAJOR"
            }
            steps {
                withSonarQubeEnv(SONARQUBE_NAME) {
                    sh """
                        ${SQ_SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.stash.url=$BITBUCKET_URL \
                            -Dsonar.stash.project=$BITBUCKET_PROJECT \
                            -Dsonar.stash.repository=$BITBUCKET_REPO \
                            -Dsonar.stash.login=$BITBUCKET_CREDS_USR \
                            -Dsonar.stash.password=$BITBUCKET_CREDS_PSW \
                            -Dsonar.stash.pullrequest.id=$CHANGE_ID \
                            -Dsonar.stash.comments.reset=true \
                            -Dsonar.stash.issue.severity.threshold=$COMMENT_SEVERITY \
                            -Dsonar.stash.notification=true \
                            -Dsonar.stash.reviewer.approval=true \
                            -Dsonar.stash.reviewer.approval.severity.threshold=$APPROVAL_SEVERITY \
                            -Dsonar.analysis.mode=issues \
                            -Dsonar.sources=. \
                            -Dsonar.projectKey=$PROJECT
                    """
                }
            }
        }
        stage('SonarQube pull request analysis - Bitbucket Cloud') {   // Obsolete, use this stage if you are using sonar-bitbucket-plugin and SonarQube 7.6 (and less)
            when {
                changeRequest()
            }
            environment {
                BITBUCKET_ACCOUNT = "<set>"
                BITBUCKET_REPO = "<set>"   
                BITBUCKET_CREDS = credentials('<jenkins credentials id for Bitbucket Cloud')
                COMMENT_SEVERITY = "MAJOR"
            }
            steps {
                withSonarQubeEnv(SONARQUBE_NAME) {
                    sh """
                        ${SQ_SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.bitbucket.repoSlug=$BITBUCKET_REPO \
                            -Dsonar.bitbucket.accountName=$BITBUCKET_ACCOUNT \
                            -Dsonar.bitbucket.teamName=$BITBUCKET_CREDS_USR \
                            -Dsonar.bitbucket.apiKey=$BITBUCKET_CREDS_PSW \
                            -Dsonar.bitbucket.pullRequestId=$CHANGE_ID \
                            -Dsonar.bitbucket.minSeverity=COMMENT_SEVERITY \
                            -Dsonar.analysis.mode=issues \
                            -Dsonar.sources=.
                    """
                }
            }
        }
        stage('SonarQube pull request analysis - Gitlab') {   // Obsolete, use this stage if you are using sonar-gitlab-plugin and SonarQube 7.5 (and less)
            <TO DO>
        }
        stage('SonarQube quality gate') {   // Catches webhook from SonarQube
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
