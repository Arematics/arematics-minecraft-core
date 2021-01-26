FROM amazoncorretto:latest

MAINTAINER "contact@arematics.com <Arematics UG (haftungsbeschränkt)>"

RUN yum install -y iputils

EXPOSE 25565
WORKDIR /data

ENV XMX=4G XMS=2G PVP=true DIFFICULTY=1 ONLINE_MODE=false LEVEL=world VIEW_DISTANCE=10 SPAWN_MONSTERS=true SPAWN_ANIMALS=true WHITELIST=false ALLOW_NETHER=true


COPY server.jar .
COPY eula.txt .
COPY server.properties .
COPY spigot.yml .
COPY start.sh .

RUN sed -i "s/XMX=4G/XMX=$XMX/g" start.sh
RUN sed -i "s/XMS=2G/XMS=$XMS/g" start.sh

RUN sed -i "s/pvp=true/pvp=$PVP/g" server.properties
RUN sed -i "s/difficulty=1/pvp=$DIFFICULTY/g" server.properties
RUN sed -i "s/level-name=world/level-name=$LEVEL/g" server.properties
RUN sed -i "s/view-distance=12/view-distance=$VIEW_DISTANCE/g" server.properties
RUN sed -i "s/spawn-monsters=true/spawn-monsters=$SPAWN_MONSTERS/g" server.properties
RUN sed -i "s/spawn-animals=true/spawn-animals=$SPAWN_ANIMALS/g" server.properties
RUN sed -i "s/white-list=false/white-list=$WHITELIST/g" server.properties
RUN sed -i "s/allow-nether=true/allow-nether=$ALLOW_NETHER/g" server.properties
RUN sed -i "s/online-mode=false/online-mode=$ONLINE_MODE/g" server.properties
RUN sed -i "s/view-distance: 12/view-distance: $VIEW_DISTANCE/g" spigot.yml
RUN sed -i "s/bungeecord: false/bungeecord: true/g" spigot.yml

ENTRYPOINT ./start.sh

VOLUME ["/data/plugins", "/data/logs"]
