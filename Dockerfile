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

RUN wget http://ftp.ps.pl/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz \
 && tar -zxvf apache-maven-3.3.3-bin.tar.gz -C /opt/ \
 && ln -s /opt/apache-maven-3.3.3/bin/mvn /usr/bin/mvn

ENV repositoriesBase /home/kosher
ENV gitlabRepositoryBase /home/git

# attach volumes
RUN mkdir -p ${repositoriesBase}/data/repositories 
RUN mkdir -p ${gitlabRepositoryBase}/data 
RUN chown jetty:jetty -R ${repositoriesBase}
RUN chown jetty:jetty -R ${gitlabRepositoryBase}
RUN groupadd --gid 1000 git && usermod -a -G git jetty # && usermod -a -G root jetty
#VOLUME ${repositoriesBase}
#VOLUME ${gitlabRepositoryBase}

RUN apt-get install -y apt-utils sudo

COPY src/docker/clone-repository.sh /home/kosher/clone-repository.sh

RUN echo "jetty ALL=(ALL) NOPASSWD:/home/kosher/clone-repository.sh" >> /etc/sudoers
RUN chmod a+x /home/kosher/clone-repository.sh

#RUN chown jetty:jetty -R ${repositoriesBase}
#RUN chown jetty:jetty -R ${gitlabRepositoryBase}



COPY target/base-listener-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.WAR

EXPOSE 8080