#!/bin/bash
set -e
set -x

mvn clean install

TEST_LIBS=target/jlinkLib
TEST_TEMP=target/jlinkTmp
TEST_RESULT=target/jlinkBin
INSTALLER_DIR=target/packageInstaller

mkdir $TEST_LIBS $TEST_TEMP

echo "Copying ..."
cp target/lib/* $TEST_LIBS
sleep 1
cp target/modules/* $TEST_LIBS

cp target/mmt-app-2.11.jar $TEST_LIBS

echo "Link ..."

# jlink @jlinkArgs

jlink \
--module-path \
  $TEST_LIBS \
--no-header-files \
--no-man-pages \
--add-modules \
javafx.base,\
javafx.fxml,\
javafx.controls,\
javafx.graphics,\
javafx.swing,\
org.apache.pdfbox,\
org.apache.pdfbox.io,\
org.apache.fontbox,\
framework.smack,\
framework.smack_jfx,\
commons.logging,\
jdk.localedata,\
app.mmt \
--output \
$TEST_RESULT \
--include-locales=en,de \
--launcher mmt=app.mmt/de.michab.app.mmt.Mmt \
--add-options " --add-modules=app.mmt"

jpackage \
-n Mmt \
--module app.mmt/de.michab.app.mmt.Mmt \
--runtime-image $TEST_RESULT \
--dest $INSTALLER_DIR

# target/mmt-jlink/bin/java --add-modules app.mmt -m app.mmt/de.michab.app.mmt.Mmt
