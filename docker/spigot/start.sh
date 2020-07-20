#!/bin/bash

XMX=4G
XMS=2G

java -cp -Xmx$XMX -Xms$XMS -jar server.jar
