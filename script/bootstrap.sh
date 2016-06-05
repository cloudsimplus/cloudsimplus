#!/bin/bash

echo -e "This script uses maven to compile all CloudSim++ sources and create the JAR packages.\n"
echo "It also allows you to run any CloudSim++ examples when you give the full name of the example class you want to run (that includes the full package name)."
echo -e "For instance, to run the CloudSimExample1 you can type: $0 org.cloudbus.cloudsim.examples.CloudSimExample1\n"

VERSION="1.0" 
CLOUDSIMPLUS="cloudsim-plus/target/cloudsim-plus-$VERSION.jar"
EXAMPLES="cloudsim-plus-examples/target/cloudsim-plus-examples-$VERSION.jar"
EXAMPLES_ROOT_PACKAGE="org.cloudbus.cloudsim.examples."

if [ "$#" -eq 0 ] || [ ! -e $CLOUDSIMPLUS ] || [ ! -e $EXAMPLES ]; then
    echo -e "Building all modules, running test suits and creating JAR files\n"
    mvn clean package
fi

if [ "$#" -eq 1 ]; then    
	echo -e "\nRunning the requested example $1\n"
    java -cp $CLOUDSIMPLUS:$EXAMPLES "$EXAMPLES_ROOT_PACKAGE$1"
fi
