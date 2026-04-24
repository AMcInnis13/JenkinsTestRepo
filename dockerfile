FROM jenkins/jenkins:lts
USER root

# Install Maven and Git
RUN apt-get update && apt-get install -y maven git && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

USER jenkins