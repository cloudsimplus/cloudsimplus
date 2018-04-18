#!/bin/bash
echo ""
echo "This script allows you to build CloudSim Plus and execute its examples."
echo "It requires maven to build all sources and create the JAR packages. Thus, make sure you have it installed."
echo "http://cloudsimplus.org"
echo ""

if [ $(dirname "$0") != "script" ]; then
   SCRIPT=$(basename "$0")
   echo "You must run this script from CloudSim Plus root directory by executing script/$SCRIPT">&2
   exit -1
fi

#The root directory of CloudSim Plus project
BASEDIR="."

#Gets the most recent jar file with a specific filename pattern
function get_examples_jar_file() {
    echo "`ls -t $BASEDIR/cloudsim-plus-examples/target/cloudsim-plus-examples-*-with-dependencies.jar | head -n 1`"
}

#call the function getting its output
EXAMPLES_JAR=$(get_examples_jar_file)

#No parameter was passed to the script. Show the usage help
if [ "$#" -eq 0 ]; then
    echo "Usage:"
    echo "	Build the project: $0 build"
    echo "	Run a specific example: $0 example_class"
    echo "		The 'example_class' has to be replaced by the fully qualified class name (that includes the package name), for instance:"
    echo "		$0 org.cloudbus.cloudsim.examples.CloudSimExample1"
    echo "		If you try to run an example before building the project, it will be built automatically"
    echo ""
    exit -1
fi

echo "CloudSim Plus Base Dir: $BASEDIR"
echo "CloudSim Plus Examples Package: $EXAMPLES_JAR"
echo ""

#If the build parameter was passed or if the examples jar doesn't exist, build the project
if [ "$1" = "build" ] || [ "$EXAMPLES_JAR" = "" ]; then
    echo "Building all modules, running test suits and creating JAR files"
    mvn clean package install
    
    if [ "$?" -ne 0 ]; then
    	echo "Error building CloudSim Plus. Check the log to try fix the build."
    	exit -1
    fi

    #If the script was built and the examples jar variable is empty,
	#the user requested to run an example before building the project.
	#The project was built, now call the function to get the jar file name again and 
	#execute the requested example
    if [ "$EXAMPLES_JAR" = "" ]; then
		EXAMPLES_JAR=$(get_examples_jar_file)
    	echo ""
    	echo "CloudSim Plus was just built. Starting the requested example."
    fi
fi

#If a parameter was passed and it is not the "build" parameter, it is expected to be an example class name.
#Thus, try to run this example.
if [ "$#" -eq 1 ] && [ "$1" != "build" ]; then    
	echo "Running the requested example $1:"
    echo "	java -cp $EXAMPLES_JAR $1"
    echo ""
    java -cp $EXAMPLES_JAR "$1"
fi
