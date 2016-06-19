# Gitlab Java Event Listener Base Application

[![Build Status](https://travis-ci.org/tunguski/kosher.svg?branch=master)](https://travis-ci.org/tunguski/kosher) 
[![Coverage Status](https://coveralls.io/repos/tunguski/kosher/badge.png?branch=master)](https://coveralls.io/r/tunguski/kosher?branch=master)

Base application for creating GitLab event listener.

## Building Kosher application

1. Maven build: ```mvn clean install```
1. Build development docker container: ```docker build -t tunguski/kosher .```
1. Run Kosher locally: ```docker run --name kosher -d -p 10081:8080 --volumes-from=gitlab_gitlab_1 tunguski/kosher```
    * Kosher may connect to Gitlab server via volume, so here gitlab_gitlab_1 container have to be created first.
    * Use https://github.com/sameersbn/docker-gitlab ```docker-compose.yml``` if you don't have Gitlab already.

## Test development version

1. Create test project in Gitlab. We use cloned Kosher project for this purpose.
1. Add Webhook that connects Gitlab build to Kosher: ```http://kosher:8080/hooks```
1. Test your webhook in Gitlab. It should start Kosher build. You can
   check if it started by looking at logs: ```docker logs -f --tail=20 kosher```
   It should produce build logs for started build.
1. Check resulted build via Kosher web view. Our test project is cloned
   as ```tunguski/kosher``` and test build starts for ```master``` branch.
   If you configure project as described here, you should be able to
   see result at http://localhost:10081/tunguski/kosher/master/index.html

## Developing Kosher style

If you want to work on your site's style, please read [Kosher-base-style README](https://github.com/tunguski/kosher-base-style)
for more information.