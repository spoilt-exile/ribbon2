#!/bin/bash

docker image rm -f freax/ribbon2-uix:3.0
docker image rm -f freax/ribbon2-gateway:3.0
docker image rm -f freax/ribbon2-directory:3.0
docker image rm -f freax/ribbon2-messenger:3.0
docker image rm -f freax/ribbon2-exchanger:3.0

docker build uix/ -t freax/ribbon2-uix:3.0
docker build gateway/ -t freax/ribbon2-gateway:3.0
docker build directory-unit/ -t freax/ribbon2-directory:3.0
docker build message-unit/ -t freax/ribbon2-messenger:3.0
docker build IO/exchanger-unit/ -t freax/ribbon2-exchanger:3.0