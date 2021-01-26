#!/bin/bash

rsync -av --ignore-existing /tmp/velocity.jar /home/proxy
rsync -av --ignore-existing /tmp/start.sh /home/proxy
rsync -av --ignore-existing /tmp/velocity.toml /home/proxy
cd /home/proxy || true
ls
./start.sh
