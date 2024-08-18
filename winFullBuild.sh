#!/bin/bash
set -e

# This is a jdk that contains the jpackager tool.  Currently
# this is a dedicated version different from our LTS11 since
# jdk 11 does not include the packager yet.
export JDK_14_HOME=/cygdrive/c/Programme/Java/jdk-14
if [ ! -d "$JDK_14_HOME" ]; then
    echo "Jdk14 not found.  Check path."
	exit 1
fi

export PLATFORM=windows

# Copy the build.number into the application resources.
# This needs to be done before everything else.

cp mmt_app/package/build.number mmt_app/src/main/java/de/michab/app/mmt/
cp mmt_sec/package/build.number mmt_sec/src/main/java/de/michab/app/mmtsec


mvn clean install -DskipTests

JLINK_SUB_PATH=target/maven-jlink
JLINK_SUB_BIN=$JLINK_SUB_PATH/bin/server

# Check if we had success creating the build.
if [ ! -e mmt_app/$JLINK_SUB_BIN/jvm.dll ]; then
    echo "No mmt_app jvm.dll"
    exit 1
fi
if [ ! -e mmt_sec/$JLINK_SUB_BIN/jvm.dll ]; then
    echo "No mmt_sec jvm.dll"
    exit 1
fi

./package/do generatex mmt_app/package/$PLATFORM/
./package/do generatex mmt_sec/package/$PLATFORM/
