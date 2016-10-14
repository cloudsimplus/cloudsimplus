#!/bin/bash
clear
echo "This script allows you to build CloudSim Plus and execute its examples"
echo "If you try to run any example before building the project, it will automatically build it for you before running the example."
echo "It uses maven to build all CloudSim++ sources and create the JAR packages."
echo "It also allows you to run any CloudSim++ examples when you give the full name of the example class you want to run (that includes the full package name)."
echo "For instance, to run the CloudSimExample1 you can type: $0 org.cloudbus.cloudsim.examples.CloudSimExample1"
echo ""

BASEDIR=$(dirname "$0")
if [ "$BASEDIR" = "." ]; then
  BASEDIR=".." 
elif [ "$BASEDIR" = "script" ]; then
  BASEDIR="." 
fi

VERSION="1.0" 
#CLOUDSIMPLUS="$BASEDIR/cloudsim-plus/target/cloudsim-plus-$VERSION.jar"
EXAMPLES="$BASEDIR/cloudsim-plus-examples/target/cloudsim-plus-examples-$VERSION.jar"

echo "Base dir: $BASEDIR"
echo "CloudSim Plus Version: $VERSION"
#echo "CloudSim Plus jar: $CLOUDSIMPLUS"
echo "CloudSim Plus Examples jar: $EXAMPLES"
echo ""

if [ "$#" -eq 0 ] || [ ! -e $CLOUDSIMPLUS ] || [ ! -e $EXAMPLES ]; then
    echo "Building all modules, running test suits and creating JAR files"
    mvn clean package install
fi

if [ "$#" -eq 1 ]; then    
	echo "Running the requested example $1"
    echo "java -cp $EXAMPLES $1"
    echo ""
    java -cp $CLOUDSIMPLUS:$EXAMPLES "$1"
fi
