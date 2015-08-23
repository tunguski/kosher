#!/usr/bin/env bash

echo "First $1"
echo "Second $2"
echo "Third $3"

mkdir -p $1
cd $1

if [ ! -d .git ]; then
  git clone $2 .
fi

git fetch
git checkout -f $3
git pull origin $3

chown jetty:jetty -R .
