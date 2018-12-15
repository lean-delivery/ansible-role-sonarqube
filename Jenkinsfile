pipeline {
	agent any
	tools {
		maven 'mvn'
	}
	environment {
		SONARQUBE_NAME = "SonarQube"
        PROJECT = sh(returnStdout: true, script: "echo $GIT_URL | cut -d/ -f5 | sed s/.git//").trim()
        EXCLUSIONS = 'dependency-check-report.html'
        MAVEN_PLUGIN = '3.5.0.1254'
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
				withSonarQubeEnv(SONARQUBE_NAME) {
					sh """
						mvn archetype:generate -DgroupId=${PROJECT} -DartifactId=${PROJECT} -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
						mv ${PROJECT}/pom.xml .
						rm -rf ${PROJECT}
						mvn org.sonarsource.scanner.maven:sonar-maven-plugin:${MAVEN_PLUGIN}:sonar --batch-mode --errors \
							-Dsonar.sources=. \
							-Dsonar.projectKey=${PROJECT} \
							-Dsonar.projectVersion=${BUILD_NUMBER} \
							-Dsonar.exclusions=${EXCLUSIONS} \
							-Dsonar.dependencyCheck.reportPath=${WORKSPACE}/dependency-check-report.xml \
							-Dsonar.dependencyCheck.htmlReportPath=${WORKSPACE}/dependency-check-report.html
						rm -f pom.xml
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