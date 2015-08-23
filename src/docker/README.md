# dbdata

    docker run -d --name dbdata tunguski/kosher-data-container

# Gitlab

    redis:
      image: sameersbn/redis:latest
    
    postgres:
      image: sameersbn/postgresql:latest
      volumes:
        - /opt/postgresql/gitlab:/var/lib/postgresql
      environment:
        - DB_USER=gitlab
        - DB_PASS=password
        - DB_NAME=gitlabhq_production
    
    gitlab:
      image: sameersbn/gitlab:latest
      volumes:
         - /var/run/docker.sock:/run/docker.sock
    #     - /opt/gitlab/data:/home/git/data
         - /var/run/docker.sock:/run/docker.sock
         - $(which docker):/bin/docker
      volumes_from: 
         - dbdata
      links:
    #     - gitlab1ci:gitlabci
         - postgres:postgresql
         - redis:redisio
      ports:
         - "10080:80"
         - "10022:22"
      environment:
         - GITLAB_HOST=172.17.42.1
         - GITLAB_PORT=10080
         - GITLAB_SSH_PORT=10022

# Gitlab CI

    postgres1ci:
      image: sameersbn/postgresql:latest
      volumes:
        - /opt/postgresql/gitlab-ci:/var/lib/postgresql
      environment:
        - DB_USER=gitlab
        - DB_PASS=password
        - DB_NAME=gitlab_ci_production
    
    gitlab1ci:
      image: sameersbn/gitlab-ci:latest
      volumes:
        - /opt/gitlab-ci/data:/home/gitlab_ci/data
        - /var/run/docker.sock:/run/docker.sock
        - $(which docker):/bin/docker
      links:
        - gitlab:gitlab
        - postgres1ci:postgresql
        - redis:redisio
      ports:
        - "10081:80"
      environment:
    #    - GITLAB_URL=http://gitlab/
        - GITLAB_APP_ID=xxx
        - GITLAB_APP_SECRET=xxx
        - GITLAB_CI_SECRETS_SESSION_KEY_BASE=xxx
        - GITLAB_CI_SECRETS_DB_KEY_BASE=xxx

# kosher

    mvn clean install -DskipTests 
    docker build -t tunguski/gitlab-java-event-listener . 
    docker rm -f gitlab-java-event-listener 
    docker run -d --name gitlab-java-event-listener -p 10082:8080 --volumes-from=dbdata tunguski/gitlab-java-event-listener:latest
