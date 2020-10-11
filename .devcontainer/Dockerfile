FROM openjdk:15-jdk-slim

ENV SDKMAN_DIR="/usr/local/sdkman"
ENV PATH=${SDKMAN_DIR}/bin:${SDKMAN_DIR}/candidates/maven/current/bin:${PATH}
COPY library-scripts/maven.sh /tmp/library-scripts/
RUN apt-get update && bash /tmp/library-scripts/maven.sh "latest" "${SDKMAN_DIR}"

RUN apt-get install gnupg2 openssh-client git -y
