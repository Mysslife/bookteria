# Identity service
This microservice is responsible for:
* Onboarding users
* Roles and permissions
* Authentication

## Tech stack
* Build tool: maven >= 3.9.5
* Java: 21
* Framework: Spring boot 3.2.x
* DBMS: MySQL

## Prerequisites
* Java SDK 21
* A MySQL server

## Start application
`mvn spring-boot:run`

## Build application
`mvn clean package`

## Docker build image + Docker push image to docker hub
`docker build -t dnthai199x1/indentity-service-0.0.1 .`
`docker image push dnthai199x1/indentity-service-0.0.1`

## Create network:
docker network create devteria-network

## Start MySQL in devteria-network
docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=HT@09021011 -d mysql:8.0.38

## Run your application in devteria-network
docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service?createDatabaseIfNotExist=true -d dnthai199x1/indentity-service-0.0.1:latest