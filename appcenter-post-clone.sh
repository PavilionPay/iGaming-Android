#!/usr/bin/env bash
set -ex
wget https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_osx-x64_bin.tar.gz
tar xvf openjdk-17_osx-x64_bin.tar.gz
export JAVA_HOME=$PWD/jdk-17.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
java -version
