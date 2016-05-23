FROM java:8

COPY target/returns-1.0-SNAPSHOT.jar ./returns.jar
COPY returns.yml .

CMD java -jar returns.jar server returns.yml
EXPOSE 8081 8180
