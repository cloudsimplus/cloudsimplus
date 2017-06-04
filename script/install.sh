#!/bin/bash
clear
echo "Builds and installs all CloudSim Plus packages, running all tests (including Integration Tests)"

START_DIR=`pwd`
SCRIPT_DIR=`dirname $0`
if [ "$START_DIR" != "$SCRIPT_DIR" ]; then
    echo -e "\nEntering into the script's directory: $SCRIPT_DIR\n" 
    cd "$SCRIPT_DIR"
fi

cd ..
mvn clean install -Dintegration-tests=true
#mvn install -pl cloudsim-plus,cloudsim-plus-examples -DskipTests=true -Dmaven.javadoc.skip=true
cd "$START_DIR"
