#!/bin/bash

if [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  rm -rf $HOME/javadoc-latest
  mkdir $HOME/javadoc-latest
  cp -R /home/travis/build/ericpinet/ConnectLife/coreserver/target/site/apidocs $HOME/javadoc-latest/coreserver
  cp -R /home/travis/build/ericpinet/ConnectLife/clapi/target/site/apidocs $HOME/javadoc-latest/clapi

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ericpinet/ConnectLife gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"
  
fi
