FROM mcr.microsoft.com/vscode/devcontainers/java:16

RUN su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven "

EXPOSE 8080