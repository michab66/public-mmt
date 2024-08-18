#!/bin/bash
set -e

jpackage --input target/lib/ --main-jar mmt-app-2.11.jar --main-class de.michab.app.mmt.Mmt
