#!/usr/bin/env bash
set -ex
wget https://download.oracle.com/java/17/archive/jdk-17.0.9_macos-x64_bin.tar.gz
tar xvf openjdk-17_osx-x64_bin.tar.gz
export JAVA_HOME=$PWD/jdk-17.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
java -version
