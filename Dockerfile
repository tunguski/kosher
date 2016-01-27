FROM java:8-jdk
MAINTAINER Marek Romanowski <marek.romanowski@gmail.com>

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN groupadd -r jetty && useradd -r -g jetty jetty

ENV JETTY_HOME /usr/local/jetty
ENV PATH $JETTY_HOME/bin:$PATH
RUN mkdir -p "$JETTY_HOME"
WORKDIR $JETTY_HOME

# see http://dev.eclipse.org/mhonarc/lists/jetty-users/msg05220.html
ENV JETTY_GPG_KEYS \
	# 1024D/8FB67BAC 2006-12-10 Joakim Erdfelt <joakime@apache.org>
	B59B67FD7904984367F931800818D9D68FB67BAC \
	# 1024D/D7C58886 2010-03-09 Jesse McConnell (signing key) <jesse.mcconnell@gmail.com>
	5DE533CB43DAF8BC3E372283E7AE839CD7C58886

RUN set -xe \
	&& for key in $JETTY_GPG_KEYS; do \
		gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key"; \
	done

ENV JETTY_VERSION 9.3.7.v20160115
ENV JETTY_TGZ_URL https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/$JETTY_VERSION/jetty-distribution-$JETTY_VERSION.tar.gz

RUN set -xe \
	&& curl -SL "$JETTY_TGZ_URL" -o jetty.tar.gz \
	&& curl -SL "$JETTY_TGZ_URL.asc" -o jetty.tar.gz.asc \
	&& gpg --verify jetty.tar.gz.asc \
	&& tar -xvf jetty.tar.gz --strip-components=1 \
	&& sed -i '/jetty-logging/d' etc/jetty.conf \
	&& rm -fr demo-base javadoc \
	&& rm jetty.tar.gz*

ENV JETTY_BASE /var/lib/jetty
RUN mkdir -p "$JETTY_BASE" && chown jetty:jetty "$JETTY_BASE"
WORKDIR $JETTY_BASE

# Get the list of modules in the default start.ini and build new base with those modules, then add setuid
RUN modules="$(grep -- ^--module= "$JETTY_HOME/start.ini" | cut -d= -f2 | paste -d, -s)" \
	&& set -xe \
	&& java -jar "$JETTY_HOME/start.jar" --add-to-startd="$modules,setuid"

ENV JETTY_RUN /run/jetty
ENV JETTY_STATE $JETTY_RUN/jetty.state
ENV TMPDIR /tmp/jetty
RUN set -xe \
	&& mkdir -p "$JETTY_RUN" "$TMPDIR" \
	&& chown -R jetty:jetty "$JETTY_RUN" "$TMPDIR"

EXPOSE 8080
CMD ["jetty.sh", "run"]




# update packages and install maven
RUN  \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y apt-utils sudo vim wget curl git

RUN wget http://ftp.ps.pl/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz \
 && tar -zxvf apache-maven-3.3.9-bin.tar.gz -C /opt/ \
 && ln -s /opt/apache-maven-3.3.9/bin/mvn /usr/bin/mvn

ENV repositoriesBase /home/kosher
ENV gitlabRepositoryBase /home/git
ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd64/

# attach volumes
RUN mkdir -p ${repositoriesBase}/data/repositories 
RUN mkdir -p ${gitlabRepositoryBase}/data 
RUN mkdir -p /home/jetty/.m2/repository
RUN chown jetty:jetty -R ${repositoriesBase}
RUN chown jetty:jetty -R ${gitlabRepositoryBase}
RUN chown jetty:jetty -R /home/jetty
RUN groupadd --gid 1000 git && usermod -a -G git jetty # && usermod -a -G root jetty
#VOLUME ${repositoriesBase}
#VOLUME ${gitlabRepositoryBase}
VOLUME /home/jetty/.m2

COPY src/docker/clone-repository.sh /home/kosher/clone-repository.sh

RUN echo "jetty ALL=(ALL) NOPASSWD:/home/kosher/clone-repository.sh" >> /etc/sudoers
RUN chmod a+x /home/kosher/clone-repository.sh

#RUN chown jetty:jetty -R ${repositoriesBase}
#RUN chown jetty:jetty -R ${gitlabRepositoryBase}

COPY target/kosher-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.WAR
