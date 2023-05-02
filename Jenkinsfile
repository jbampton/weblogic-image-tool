pipeline {
    options {
        disableConcurrentBuilds()
    }
    agent any
    tools {
        maven 'maven-3.8.7'
        jdk 'jdk11'
    }

    triggers {
        // timer trigger for "nightly build" on main branch
        cron( env.BRANCH_NAME.equals('main') ? 'H H(0-3) * * 1-5' : '')
    }

    environment {
        // variables for SystemTest stages (integration tests)
        STAGING_DIR = "${WORKSPACE}/wit-system-test-files"
        DB_IMAGE = "${WKT_OCIR_HOST}/${WKT_TENANCY}/database/enterprise:12.2.0.1-slim"
        JRE_IMAGE = "${WKT_OCIR_HOST}/${WKT_TENANCY}/java/serverjre:8"
    }

    stages {
        stage ('Environment') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    mvn --version
                '''
            }
        }
        stage ('Build') {
            steps {
                withMaven(globalMavenSettingsConfig: 'wkt-maven-settings-xml', publisherStrategy: 'EXPLICIT') {
                    sh 'mvn -B -DskipTests -DskipITs clean install'
                }
            }
        }
        stage ('Unit Tests') {
            when {
                not { changelog '\\[skip-ci\\].*' }
            }
            steps {
                withMaven(globalMavenSettingsConfig: 'wkt-maven-settings-xml', publisherStrategy: 'EXPLICIT') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    junit 'imagetool/target/surefire-reports/*.xml'
                }
            }
        }
//        stage ('Sonar Analysis') {
//            when {
//                anyOf {
//                    changeRequest()
//                    branch "main"
//                }
//            }
//            tools {
//                maven 'maven-3.8.7'
//                jdk 'jdk11'
//            }
//            steps {
//                withSonarQubeEnv('SonarCloud') {
//                    withCredentials([string(credentialsId: 'encj_github_token', variable: 'GITHUB_TOKEN')]) {
//                        runSonarScanner()
//                    }
//                }
//            }
//        }
        stage ('Download System Test Files') {
            when {
                anyOf {
                    changeRequest target: 'main'
                    triggeredBy 'TimerTrigger'
                    changelog '\\[full-mats\\].*'
                }
            }
            steps {
                sh '''
                    rm -rf ${STAGING_DIR}
                    mkdir ${STAGING_DIR}
                    oci os object bulk-download --namespace devweblogic --bucket-name wit-system-test-files --config-file=/dev/null --auth=instance_principal --download-dir ${STAGING_DIR}
                '''
            }
        }
        stage ('SystemTest Gate') {
            when {
                allOf {
                    changeRequest target: 'main'
                    not { changelog '\\[skip-ci\\].*' }
                    not { changelog '\\[full-mats\\].*' }
                }
            }
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'wkt-otn-credential', passwordVariable: 'ORACLE_SUPPORT_PASSWORD', usernameVariable: 'ORACLE_SUPPORT_USERNAME']]) {
                    withMaven(globalMavenSettingsConfig: 'wkt-maven-settings-xml', publisherStrategy: 'EXPLICIT') {
                        sh '''
                            cd tests
                            mvn -B clean verify -Dtest.groups=gate -DskipITs=false
                        '''
                    }
                }
            }
            post {
                always {
                    junit 'tests/target/failsafe-reports/*.xml'
                }
                failure {
                    archiveArtifacts artifacts: 'tests/target/logs/*.*', onlyIfSuccessful: 'false'
                }
            }
        }
        stage ('SystemTest Full') {
            when {
                anyOf {
                    triggeredBy 'TimerTrigger'
                    changelog '\\[full-mats\\].*'
                }
            }
            steps {
                script {
                    docker.image("${env.DB_IMAGE}").pull()
                    docker.image("${env.JRE_IMAGE}").pull()
                }
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'wkt-otn-credential', passwordVariable: 'ORACLE_SUPPORT_PASSWORD', usernameVariable: 'ORACLE_SUPPORT_USERNAME']]) {
                    withMaven(globalMavenSettingsConfig: 'wkt-maven-settings-xml', publisherStrategy: 'EXPLICIT') {
                        sh '''
                            cd tests
                            mvn -B clean verify -Dtest.groups=gate,nightly -DskipITs=false
                        '''
                    }
                }
            }
            post {
                always {
                    junit 'tests/target/failsafe-reports/*.xml'
                }
                failure {
                    // save logs for debugging test failures
                    archiveArtifacts artifacts: 'tests/target/logs/*.*', onlyIfSuccessful: 'false'
                    // notify staff when nightly builds fail
                    slackSend channel: '#wkt-build-failure-notifications', botUser: false, color: 'danger',
                            message: "Build failed. WebLogic Image Tool: <${env.BUILD_URL}|${env.JOB_NAME}:${env.BUILD_NUMBER}>"
                }
            }
        }
        stage ('Save Nightly Installer') {
            when {
                triggeredBy 'TimerTrigger'
                branch "main"
            }
            steps {
                sh '''
                    oci os object put --namespace=${WKT_TENANCY} --bucket-name=wko-system-test-files --config-file=/dev/null --auth=instance_principal --force --file=installer/target/imagetool.zip --name=imagetool-main.zip
                '''
            }
        }
        stage ('Sync') {
            when {
                branch 'main'
                anyOf {
                    not { triggeredBy 'TimerTrigger' }
                    tag 'release-*'
                }
            }
            steps {
                build job: "wkt-sync",
                        parameters: [ string(name: 'REPOSITORY', value: 'weblogic-image-tool') ],
                        wait: true
            }
        }
        stage ('Create Draft Release') {
            when {
                tag 'release-*'
            }
            steps {
                script {
                    env.TAG_VERSION_NUMBER = env.TAG_NAME.replaceAll('release-','').trim()
                }
                withCredentials([string(credentialsId: 'wkt-github-token', variable: 'GITHUB_API_TOKEN')]) {
                    sh """
                    mkdir gh-cli
                    curl -sL https://github.com/cli/cli/releases/download/v2.28.0/gh_2.28.0_linux_amd64.tar.gz | tar xvzf - --strip-components=1 -C ./gh-cli
                    echo '${GITHUB_API_TOKEN}' | ./gh-cli/bin/gh auth login --with-token
                    ./gh-cli/bin/gh release create ${TAG_NAME} \
                        --draft \
                        --generate-notes \
                        --title 'WebLogic Image Tool ${TAG_VERSION_NUMBER}' \
                        --repo https://github.com/oracle/weblogic-image-tool \
                        installer/target/imagetool.zip
                    """
                }
            }
        }
    }
}

//void runSonarScanner() {
//    def changeUrl = env.GIT_URL.split("/")
//    def org = changeUrl[3]
//    def repo = changeUrl[4].substring(0, changeUrl[4].length() - 4)
//    if (env.CHANGE_ID != null) {
//        sh "mvn -B sonar:sonar \
//            -Dsonar.projectKey=${org}_${repo} \
//            -Dsonar.pullrequest.provider=GitHub \
//            -Dsonar.pullrequest.github.repository=${org}/${repo} \
//            -Dsonar.pullrequest.key=${env.CHANGE_ID} \
//            -Dsonar.pullrequest.branch=${env.CHANGE_BRANCH} \
//            -Dsonar.pullrequest.base=${env.CHANGE_TARGET}"
//    } else {
//        sh "mvn -B sonar:sonar \
//           -Dsonar.projectKey=${org}_${repo} \
//           -Dsonar.branch.name=${env.BRANCH_NAME}"
//    }
//}
