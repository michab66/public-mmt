#!/bin/bash
set -e

#NOTE:  This will be unified whith winFullBuild, no new functionality here!

# This is a jdk that contains the jpackager tool.  Currently
# this is a dedicated version different from our LTS11 since
# jdk 11 does not include the packager yet.
export JDK_14_HOME=~/jdk-14.jdk/Contents/Home
if [ ! -d "$JDK_14_HOME" ]; then
    echo "Jdk14 not found.  Check path."
	exit 1
fi

export PLATFORM=mac

mvn clean install -DskipTests

./package/do generatex mmt_app/package/$PLATFORM/
./package/do generatex mmt_sec/package/$PLATFORM/
