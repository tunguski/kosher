FROM jetty:9.3
MAINTAINER Marek Romanowski <marek.romanowski@gmail.com>

# update packages and install maven
RUN  \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y vim wget curl git ruby ruby-dev make gcc nodejs

RUN	ruby -S gem install jekyll

RUN wget http://ftp.ps.pl/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
RUN tar -zxvf apache-maven-3.3.3-bin.tar.gz -C /opt/
RUN ln -s /opt/apache-maven-3.3.3/bin/mvn /usr/bin/mvn

ENV repositoriesBase /repositories/
ENV gitlabRepositoryBase /gitlabRepository/

# attach volumes
RUN mkdir ${repositoriesBase}
RUN chown jetty:jetty -R ${repositoriesBase}
VOLUME ${repositoriesBase}
VOLUME ${gitlabRepositoryBase}

COPY target/base-listener-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.WAR

EXPOSE 8080