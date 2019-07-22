FROM openjdk:11
COPY rest-api-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/src
WORKDIR /usr/src/
EXPOSE 4567

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

CMD /wait && java -jar rest-api-1.0-SNAPSHOT-jar-with-dependencies.jar