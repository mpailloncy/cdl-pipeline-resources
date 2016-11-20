#!/usr/bin/env groovy

stage("build & unit tests") {
    node("build") {
        sleep 10
    }
}

stage("integration-tests") {
    node("test") {
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
        sleep 10
    }
}

stage("manual-approval") {
    input "Deploiement en production ?"
}

stage("deploy") {
    node("ssh") {
        sleep 10
    }
}
