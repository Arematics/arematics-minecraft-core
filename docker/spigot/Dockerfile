FROM amazoncorretto:latest

MAINTAINER "Arematics UG (haftungsbeschränkt)"

VOLUME ["/gameserver"]

ENV XMX="4G"
ENV XMS="2G"

WORKDIR /gameserver

ADD server.jar .
ADD start.sh .
ADD eula.txt .
ADD server.properties .

RUN sed -i "s/XMX=4G/XMX=$XMX/g" start.sh
RUN sed -i "s/XMS=2G/XMS=$XMS/g" start.sh

EXPOSE 25565

CMD /gameserver/start.sh