def getStatusFromBuildStatus() {

    if (currentBuild.result == null || "SUCCESS" == currentBuild.result) {
        return "SUCCESS"
    } else if ("FAILED" == currentBuild.result) {
        return "FAILURE"
    } else {
        return "ERROR"
    }
}
node {

    def scmInfo = checkout scm
    githubNotify account: 'Ameren', context: 'Build Status', credentialsId: 'jenkins-api-token',
        description: 'This is an example', repo: 'outage-order-core', sha: "${scmInfo.GIT_COMMIT}", gitApiUrl: 'https://github.ameren.com/api/v3', targetUrl: "${env.RUN_DISPLAY_URL}", status: 'PENDING'
    try {
        podTemplate(){
            node('pod-dind') {
                container('jnlp-custom-one') {
                    sh "openssl s_client -showcerts -connect artifactory.ameren.com:443 </dev/null 2>/dev/null|openssl x509 -outform PEM > ${env.WORKSPACE}/artifactory.pem"
                    sh "chmod +x artifactory.pem"
                }
                container('centos') {
                    def mvnHome
                    mvnHome = tool 'maven-3.5.4'
                    jdk = tool name: 'JDK-Tool'
                    env.JAVA_HOME = "${jdk}"
                    sh "curl -O https://confluence.atlassian.com/kb/files/779355358/779355357/1/1441897666313/SSLPoke.class"
                    sh "cd $JAVA_HOME/jre/lib/security && chmod +x cacerts"
                    sh "$jdk/bin/keytool -noprompt -storepass changeit -trustcacerts -importcert -alias artifactory -file $WORKSPACE/artifactory.pem -keystore $JAVA_HOME/jre/lib/security/cacerts"
                    sh "$jdk/bin/java SSLPoke artifactory.ameren.com 443"

                    stage('Checkout') {

                        checkout scm
                    }

                    stage('Build') {
                        configFileProvider(
                            [configFile(fileId: '8c8759ed-5b07-4823-9061-ceac68f26ea1', variable: 'MAVEN_SETTINGS')]) {
                            sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS clean install"
                        }
                    }
                    stage('code quality analysis') {
                        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonar-creds', passwordVariable: 'pwd', usernameVariable: 'sonarUsr']]) {

                            String name = 'outage-order-core'
                            String groupId = 'com.ameren.outage.api'

                            withSonarQubeEnv('sonarqube-prod') {
                                configFileProvider([configFile(fileId: '8c8759ed-5b07-4823-9061-ceac68f26ea1', variable: 'MAVEN_SETTINGS')]) {
                                    sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS  sonar:sonar -Dsonar.projectKey=${groupId}:${name}"
                                }
                            }
                            // No need to occupy a node
                            //stage("Quality Gate"){

                             //   sh "sleep 60"
                             //   timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout
                                    // Reuse taskId previously collected by withSonarQubeEnv
                             //       if (waitForQualityGate().status != 'OK') {
                             //           error "Pipeline aborted due to quality gate failure: ${waitForQualityGate().status}"
                             //       }
                              //  }
                            //}

                        }
                    }

                    stage('Deploy') {
                        configFileProvider(
                            [configFile(fileId: '8c8759ed-5b07-4823-9061-ceac68f26ea1', variable: 'MAVEN_SETTINGS')]) {
                            sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS deploy"
                        }
                    }
                }
            }
        }
    }
    catch (e) {
        // If there was an exception thrown, the build failed
        currentBuild.result = "FAILED"
        throw e
    } finally {
        // Success or failure, always send notifications
        notifyBuild(currentBuild.result)
        githubNotify account: 'Ameren', context: 'Build Status', credentialsId: 'jenkins-api-token',
            description: 'This is an example', repo: 'outage-order-core', sha: "${scmInfo.GIT_COMMIT}", gitApiUrl: 'https://github.ameren.com/api/v3', targetUrl: "${env.RUN_DISPLAY_URL}", status: getStatusFromBuildStatus()
    }
}

def notifyBuild(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESSFUL'

    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    // Send notifications
    emailext(
        body: '''${SCRIPT, template="groovy-text.template"}''', replyTo: 'UChaudhary@ameren.com, OutageProductDev@ameren.com, ARaskar@ameren.com', subject: subject, to: 'UChaudhary@ameren.com, OutageProductDev@ameren.com, ARaskar@ameren.com'
    )
}
