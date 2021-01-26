FROM amazoncorretto:latest

MAINTAINER "contact@arematics.com <Arematics UG (haftungsbeschränkt)>"

RUN mkdir /data

EXPOSE 25565

WORKDIR /data

COPY velocity.jar .
COPY start.sh .
COPY velocity.toml .

ENTRYPOINT ./start.sh

VOLUME ["/data/plugins", "/data/logs"]
