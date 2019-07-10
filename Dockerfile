FROM openjdk:11-jdk-slim

LABEL maintainer = "biosphere.dev@gmx.de"

ADD src/main/resources/static ./src/main/resources/static

COPY target/spoticord-1.0-SNAPSHOT-shaded.jar spoticord.jar

ENTRYPOINT ["java", "-jar", "-Xmx128m", "spoticord.jar"]