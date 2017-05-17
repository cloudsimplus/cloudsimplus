#!/bin/bash

clear
echo "Parses JavaDocs from Java Source Files and generate them in AsciiDoc format using the ExportDoclet maven plugin"
echo "https://github.com/johncarl81/exportdoclet"

START_DIR=`pwd`
SCRIPT_DIR=`dirname $0`
if [ "$START_DIR" != "$SCRIPT_DIR" ]; then
    echo -e "\nEntering into the script's directory: $SCRIPT_DIR\n" 
    cd "$SCRIPT_DIR"
fi

#Directory to generate the AsciiDoc files
TMP="/tmp/javadocs"
mkdir -p $TMP
rm -rf "$TMP/*"

#Maven Local Repository
REPO="$HOME/.m2/repository"

#The maven doclet plugin for JavaDoc to export javadoc comments from java files to AsciiDoc files
DOCLETJAR="$REPO/org/asciidoctor/exportdoclet/1.5.4-SNAPSHOT/exportdoclet-1.5.4-SNAPSHOT.jar"
DEPS="$REPO/org/cloudsimplus/cloudsim-plus/1.2.0/cloudsim-plus-1.2.0.jar:$REPO/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar:$REPO/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar"

#CloudSim Plus source dir
SRC="../cloudsim-plus/src/main/java/org/cloudsimplus"

find "$SRC" -type f -name "*.java" > "$TMP/list.txt"

#Extracts the javadocs from java files into AsciiDoc files
javadoc -cp $DEPS -docletpath $DOCLETJAR -doclet org.asciidoctor.ExportDoclet -d $TMP -author -version @$TMP/list.txt
echo ""
echo "JavaDocs from java source files exported to AsciiDoc files into the $TMP directory."