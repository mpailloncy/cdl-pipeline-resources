#!/usr/bin/env groovy

@Library("cdl-shared") _

stage("build & unit tests") {
    node("build") {
        checkout scm
        docker.image('maven:3.3.9-jdk-8').inside {
            sh "mvn clean package"
            stash includes: 'target/simple-app-1.0-SNAPSHOT.jar', name: 'binary'
        }
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

stage("staging") {
    node("ssh") {
        deleteDir()
        deployBinary("178.62.107.76")
    }
}

stage("manual-approval") {
    input "Deploiement en production ?"
}

stage("deploy") {
    node("ssh") {
        deleteDir()
        deployBinary("178.62.107.76")
    }
}

def deployBinary(hostname) {
    unstash "binary"
    sshagent(["deploy_key"]) {
        scp("root", hostname, "target/simple-app-1.0-SNAPSHOT.jar", "/root/")
        sshExec("root", hostname, "ls -rtl /root/")
    }
}