#!/bin/bash
#set -e
set -x

TEST_LIBS=target/testBuildLib
TEST_TEMP=target/testBuildTmp
TEST_RESULT=target/testBuildBin

echo "Delete ..."
rm -rf $TEST_LIBS $TEST_TEMP $TEST_RESULT

mkdir $TEST_LIBS $TEST_TEMP $TEST_RESULT

echo "Copying ..."
cp target/lib/* $TEST_LIBS
sleep 1
cp target/modules/* $TEST_LIBS

cp target/mmt-app-2.11.jar $TEST_LIBS

echo "Package ..."
jpackage --verbose --temp $TEST_TEMP --dest $TEST_RESULT --module-path $TEST_LIBS --module app.mmt/de.michab.app.mmt.Mmt
