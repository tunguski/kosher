FROM jetty:9.3
MAINTAINER Marek Romanowski <marek.romanowski@gmail.com>

WORKDIR .

#RUN mkdir /var/lib/jetty/webapps/
COPY target/base-listener-1.0-SNAPSHOT.war /var/lib/jetty/webapps/

EXPOSE 8080