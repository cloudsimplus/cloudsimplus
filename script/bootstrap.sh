#!/bin/bash
echo ""
echo "This script allows you to build CloudSim Plus and execute its examples."
echo "It requires maven to build all sources and create the JAR packages. Thus, make sure you have it installed."
echo "https://cloudsimplus.org"
echo ""

if [ "$(dirname "$0")" != "script" ]; then
   SCRIPT=$(basename "$0")
   echo "You must run this script from CloudSim Plus root directory by executing script/$SCRIPT">&2
   exit 1
fi

#The root directory of CloudSim Plus project
BASEDIR="."
EXAMPLES="cloudsim-plus-examples"

#Gets the most recent jar file with a specific filename pattern
function get_examples_jar_file() {
  find "$BASEDIR/$EXAMPLES/target/" -name "$EXAMPLES-*-with-dependencies.jar" | head -n 1
}

EXAMPLES_JAR=$(get_examples_jar_file)

#No parameter was passed to the script. Show the usage help
if [ "$#" -eq 0 ]; then
    echo "Usage:"
    echo "	Build the project: $0 build"
    echo "	Run a specific example: $0 example_class"
    echo "		The 'example_class' has to be replaced by the fully qualified class name (that includes the package name), for instance:"
    echo "		$0 org.cloudsimplus.examples.BasicFirstExample"
    echo "		If you try to run an example before building the project, it will be built automatically"
    echo ""
    exit 1
fi

echo "CloudSim Plus Base Dir: $BASEDIR"
echo "CloudSim Plus Examples Package: $EXAMPLES_JAR"
echo ""

#If the build parameter was passed or if the examples jar doesn't exist, build the project
if [ "$1" = "build" ] || [ "$EXAMPLES_JAR" = "" ]; then
    echo "Building all modules, running test suits and creating JAR files"

    if mvn clean package install; then
    	echo "Error building CloudSim Plus. Check the log to try fix the build."
    	exit 1
    fi

    #If the examples jar variable is empty,
    #the user requested to run an example before building the project.
    #Here, the project was automatically built, then execute the requested example
    if [ "$EXAMPLES_JAR" = "" ]; then
		  EXAMPLES_JAR=$(get_examples_jar_file)
    	echo ""
    	echo "CloudSim Plus was just built. Starting the requested example."
    fi
fi

#If a parameter was passed and it is not equals to "build",
# it is expected to be a fully-qualified example class name. Thus, try to run that example.
if [ "$#" -eq 1 ] && [ "$1" != "build" ]; then    
	  echo "Running the requested example $1:"
    echo "	java -cp $EXAMPLES_JAR $1"
    echo ""
    java -cp "$EXAMPLES_JAR" "$1"
fi
