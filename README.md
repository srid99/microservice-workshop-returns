## Microservices workshop

[![Build Status](https://travis-ci.org/srid99/microservice-workshop-returns.svg?branch=master)](https://travis-ci.org/srid99/microservice-workshop-returns)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/46a68773dec24b0198e9171f9af56ae3)](https://www.codacy.com/app/srid99/microservice-workshop-returns?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=srid99/microservice-workshop-returns&amp;utm_campaign=Badge_Grade)
[![Codeship Badge](https://codeship.com/projects/c2735500-0022-0134-dd17-62bf2e8b1bf1/status?branch=master)](https://codeship.com/projects/153089)

A two day Microservices workshop where teams have to build a service and integrate with other services/teams to get the actual product working.


Simple cookbook for locally running the application:

1. Start consul, e.g. like this:
```shell
consul agent -data-dir=/tmp/consul -server -bootstrap-expect 1 -ui-dir ./consul/consul_0.6.4_web_ui -bind 127.0.0.1
```
2. Build and run the dropwizard application:
```shell
mvn clean package
java -jar target/returns-1.0-SNAPSHOT.jar server returns.yml
```
3. Post a returns object:
```shell
curl -X POST -H "Content-Type: application/json" -d '{"orderNumber": 42}' http://localhost:8081/returns
```
4. Show api documentation:
```shell
chrome http://localhost:8081/swagger
```


