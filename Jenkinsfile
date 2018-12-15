pipeline {
	agent any
	environment {
		SONARQUBE_NAME = "SonarQube"
	}
	stages {
		stage('SonarQube pull request analysis') {
			when {
				changeRequest()
			}
			steps {
				withSonarQubeEnv(SONARQUBE_NAME) {
					sh """
					"""
				}
			}
		}
		stage('SonarQube branch analysis') {
			when {
				not { changeRequest() }
			}
			steps {
				dependencyCheckAnalyzer scanpath: WORKSPACE, outdir: WORKSPACE, datadir: '/tmp', suppressionFile: '', hintsFile: '', zipExtensions: '',  isAutoupdateDisabled: false, includeHtmlReports: true, includeVulnReports: false, includeJsonReports: false, includeCsvReports: false, skipOnScmChange: false, skipOnUpstreamChange: false
				dependencyCheckPublisher canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '', unHealthy: ''
				archiveArtifacts allowEmptyArchive: true, artifacts: '**/dependency-check-report.xml', onlyIfSuccessful: true
				withSonarQubeEnv(SONARQUBE_NAME) {
					sh """
					"""
				}
			}
		}
		stage('SonarQube quality Gate') {
			when {
				not { changeRequest() }
			}
			agent none
			steps {
				timeout(time: 1, unit: 'HOURS') {
					script {
						def qg = waitForQualityGate()
						if (qg.status != 'OK') {
							error "Pipeline aborted due to quality gate failure: ${qg.status}"
						}
					}
				}
			}
		}
	}
	post {
		cleanup {
			cleanWs()
		}
	}
}