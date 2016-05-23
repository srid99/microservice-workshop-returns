## Microservices workshop

[![Build Status](https://travis-ci.org/srid99/microservice-workshop-returns.svg?branch=master)](https://travis-ci.org/srid99/microservice-workshop-returns)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/46a68773dec24b0198e9171f9af56ae3)](https://www.codacy.com/app/srid99/microservice-workshop-returns)
[![Code Coverage](https://api.codacy.com/project/badge/coverage/46a68773dec24b0198e9171f9af56ae3)](https://www.codacy.com/app/srid99/microservice-workshop-returns)
[![Docker Pulls](https://img.shields.io/docker/pulls/srid99/microservices-workshop.svg?maxAge=2592000)](https://hub.docker.com/r/srid99/microservices-workshop/)

A two day Microservices workshop where teams have to build a service and integrate with other services/teams to get the actual product working.


Simple cookbook for locally running the application:

* Start consul, e.g. like this:
```shell
consul agent -data-dir=/tmp/consul -server -bootstrap-expect 1 -ui-dir ./consul/consul_0.6.4_web_ui -bind 127.0.0.1
```
* Build and run the dropwizard application:
```shell
mvn clean package
java -jar target/returns-1.0-SNAPSHOT.jar server returns.yml
```
* Post a returns object:
```shell
curl -X POST -H "Content-Type: application/json" -d '{"orderNumber": 42}' http://localhost:8081/returns
```
* Show api documentation:
```shell
chrome http://localhost:8081/swagger
```
