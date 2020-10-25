FROM maven:3.6.3-openjdk-15 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:15-jdk-slim
COPY --from=build /usr/src/app/target/spoticord-*-SNAPSHOT-shaded.jar spoticord.jar

EXPOSE 8080  
ENTRYPOINT ["java", "-jar", "--enable-preview", "spoticord.jar" ]
