FROM jenkins:2.19.3-alpine

RUN /usr/local/bin/install-plugins.sh \ 
	workflow-aggregator:2.4 \
	pipeline-stage-view:2.2 \
	workflow-multibranch:2.9.2 \
	pipeline-utility-steps:1.1.6 \
	pipeline-model-definition:0.6 \
	blueocean:1.0.0-b12 \	
	github-branch-source:1.10 \ 
	cloudbees-bitbucket-branch-source:1.8 \
	digitalocean-plugin:0.12 \
	ssh-agent:1.13

COPY init.groovy /usr/share/jenkins/ref/init.groovy.d/init.groovy

ENV JAVA_OPTS "-Djenkins.install.runSetupWizard=false"
