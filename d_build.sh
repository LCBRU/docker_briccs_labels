#!/usr/bin/env bash

docker build -t lcbruit/briccs_label:v1.1 .
docker run -itd --name labels -p 8080:8080 lcbruit/briccs_label:v1.1
