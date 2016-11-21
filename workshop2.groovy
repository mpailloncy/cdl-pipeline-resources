#!/usr/bin/env groovy

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

stage("staging") {
    node("ssh") {
        deleteDir()
        unstash "binary"
        sh "ls -rtl"
    }
}

stage("manual-approval") {
    input "Deploiement en production ?"
}

stage("deploy") {
    node("ssh") {
        deleteDir()
        unstash "binary"
        sh "ls -l target"
    }
}
