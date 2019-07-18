FROM openjdk:11
COPY rest-api-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/src
WORKDIR /usr/src/
EXPOSE 4567
CMD ["java","-jar","rest-api-1.0-SNAPSHOT-jar-with-dependencies.jar"]
