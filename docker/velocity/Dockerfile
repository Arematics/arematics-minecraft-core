FROM amazoncorretto:latest

MAINTAINER "contact@arematics.com <Arematics UG (haftungsbeschränkt)>"

RUN yum install -y rsync

EXPOSE 25565

RUN mkdir /home/proxy
WORKDIR /tmp/

ADD velocity.jar .
ADD start.sh .
ADD velocity.toml .

WORKDIR /data

COPY start /
RUN chmod 777 start

ENTRYPOINT [ "/start" ]
