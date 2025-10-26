#!/usr/bin/env bash

java -jar microservices/candidate-composite-service/build/libs/*.jar &
java -jar microservices/candidate-service/build/libs/*.jar &
java -jar microservices/comment-service/build/libs/*.jar &
java -jar microservices/news-article-service/build/libs/*.jar &
