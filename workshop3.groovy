#!/usr/bin/env groovy

@Library("cdl-shared") _

stage("build & unit tests") {
    node("build") {
        checkout scm
        def mvnHome = tool "maven-3.3.9"
        sh "${mvnHome}/bin/mvn clean package"
        stash includes: 'target/simple-app-1.0-SNAPSHOT.jar', name: 'binary'
    }
}

stage("integration-tests") {
    node("build") {
        sleep 10
    }
}

stage("acceptance-tests") {

    def tests = [
            "firefox" : {
                sleep 10
            },
            "chrome" : {
                sleep 10
            },
            "edge" : {
                sleep 10
            }
    ]

    node("test") {
        parallel tests
    }

}

if (env.BRANCH_NAME == "master") {
    stage("staging") {
        node("ssh") {
            deleteDir()
            deployBinary("<ENV_IP>")
        }
    }

    stage("manual-approval") {
        input "Deploiement en production ?"
    }

    stage("deploy") {
        node("ssh") {
            deleteDir()
            deployBinary("<PROD_IP>")
        }
    }
}

def deployBinary(hostname) {
    unstash "binary"
    sshagent(["deploy_key"]) {
        scp("root", hostname, "target/simple-app-1.0-SNAPSHOT.jar", "/root/")
        sshExec("root", hostname, "ls -rtl /root/")
    }
}

