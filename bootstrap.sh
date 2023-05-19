#!/bin/bash

mvn clean package --batch-mode --errors
docker rm --force $(docker ps -aq)
docker rmi --force user-api-application:latest
docker-compose up --detach