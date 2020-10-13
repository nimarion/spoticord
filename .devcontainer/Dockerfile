FROM openjdk:15-jdk-slim

ENV SDKMAN_DIR="/usr/local/sdkman"
ENV PATH=${SDKMAN_DIR}/bin:${SDKMAN_DIR}/candidates/maven/current/bin:${PATH}
COPY library-scripts/maven.sh /tmp/library-scripts/
RUN apt-get update && bash /tmp/library-scripts/maven.sh "latest" "${SDKMAN_DIR}"

RUN apt-get install  gpg openssh-client git -y

COPY library-scripts/zsh.sh /tmp/library-scripts/
RUN bash /tmp/library-scripts/zsh.sh

EXPOSE 8080