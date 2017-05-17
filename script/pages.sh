#!/bin/bash

clear
echo "Updates CloudSim Plus GitHub webpage by pushing files to the gh-pages branch."

START_DIR=`pwd`

SCRIPT_DIR=`dirname $0`
if [ "$START_DIR" != "$SCRIPT_DIR" ]; then
    echo -e "\nEntering into the script's directory: $SCRIPT_DIR\n" 
    cd "$SCRIPT_DIR"
fi

GITHUB_PROFILE_CLONE_URL="git@github.com:manoelcampos"
GITHUB_REPO_NAME="cloudsim-plus"
TMP_DIR="/tmp"

TMP_REPO_DIR="$TMP_DIR/$RANDOM"
SOURCE_DIR="$START_DIR/.."
cd $TMP_DIR || exit -1
echo -e "Start Dir:                         $START_DIR"
echo -e "Temp Repo Dir for gh-pages branch: $TMP_REPO_DIR (where the branch will be cloned for local update)\n"

#Removes a temp dir given as parameter and kills the script due to an error.
clean () {
   echo "Removing Temp Dir: $1"
   rm -rf "$1"
   exit -1
}

echo "Cloning the gh-pages branch"
git clone "$GITHUB_PROFILE_CLONE_URL/$GITHUB_REPO_NAME.git" -b gh-pages $TMP_REPO_DIR || clean $TMP_REPO_DIR
cd $TMP_REPO_DIR || exit -1
echo -e "\nTemp Repository Dir: $TMP_REPO_DIR\n"
git checkout gh-pages || clean $TMP_REPO_DIR

echo "Copying README.md to index.md"
cp "$SOURCE_DIR/README.md" "$TMP_REPO_DIR/index.md"
git add index.md

echo "Copying Presentation Images"
IMGS="$SOURCE_DIR/docs/presentation/images"
cp -r "$IMGS" "$TMP_REPO_DIR"
git add images/*.png
git add images/*.gif

echo "Copying HTML files"
cp "$SOURCE_DIR/docs/CloudSim-and-CloudSimPlus-Comparison.html" "$TMP_REPO_DIR/"
git add CloudSim-and-CloudSimPlus-Comparison.html
cp "$SOURCE_DIR/docs/maven.html" "$TMP_REPO_DIR/"
git add maven.html
SLIDES="$SOURCE_DIR/docs/presentation"
rsync -a --exclude="$SLIDES/node_modules" --exclude="$SLIDES/README.md" --exclude="$SLIDES/LICENSE" "$SLIDES" "$TMP_REPO_DIR/" 
git add presentation

echo "Commiting"
git commit -m "Updated index.md" || clean $TMP_REPO_DIR

echo "Pushing to remote repository"
git push origin gh-pages && echo "Done! GitHub pages updated"

rm -rf $TMP_REPO_DIR