#!/bin/bash

rsync -av --ignore-existing /tmp/server.properties /home/minecraft
rsync -av --ignore-existing /tmp/eula.txt /home/minecraft
rsync -av --ignore-existing /tmp/start.sh /home/minecraft
rsync -av --ignore-existing /tmp/spigot.yml /home/minecraft
rsync -av --ignore-existing /tmp/server.jar /home/minecraft
cd /home/minecraft || true
ls
./start.sh