#!/bin/sh
######################################################################
# A script for upgrading Forrest's jars with those from Cocoon.  Use at own
# risk!  Make sure you build Cocoon before running this.  If your cocoon-2.1
# directory is not on the same level as xml-forrest/, set the COCOON variable
# below.
#
## MUST BE OVERRIDDEN:
COCOON=../../../cocoon-2.1
FORREST=../..
#
######################################################################

CLIB=$COCOON/lib/*
CBUILD=$COCOON/build/cocoon-2.1-dev
CBLOCKS=$CBUILD/blocks

FDIST=$FORREST/build/dist/shbat

## Determines where to copy new jars to:
#FLIB=$FORREST/lib/core
#FLIB=$FORREST/build/webapp/WEB-INF/lib
FLIB=$FDIST/WEB-INF/lib

DATE=`date +%Y%m%d`

function checkdir()
{
  if [ ! -d "$1" ]; then 
    echo "Directory $1 does not exist. Please check the \$$2 variable in $0"
    exit
  fi
}

function sanity_check()
{
  checkdir "$FORREST" FORREST
  checkdir "$COCOON" COCOON
  checkdir "$FLIB" FLIB
}

function copy()
{
    rm $FLIB/$1*
    cp $CLIB/$1* $FLIB
}
function zcopy()
{
    rm $FLIB/$1*
    cp $CBUILD/$1* $FLIB
}

function bzcopy()
{
    rm $FLIB/cocoon-$1-block-*.jar
    cp $CBLOCKS/$1-block.jar $FLIB/cocoon-$1-block-$DATE.jar
}

function bcopy()
{
    rm $FLIB/$1*
    cp $COCOON/src/blocks/*/lib/$1* $FLIB/
}

sanity_check

#set -vx
#avalon-framework-4.1.3.jar
copy avalon-framework
#batik-all-1.5b2.jar
bcopy batik-all
#chaperon-20030208.jar
bcopy chaperon
#cocoon-20030311.jar
rm $FLIB/cocoon-*.jar ; cp $CBUILD/cocoon.jar $FLIB/cocoon-$DATE.jar
rm $FLIB/cocoon-deprecated*.jar ; cp $CBUILD/cocoon-deprecated.jar $FLIB/cocoon-deprecated-$DATE.jar
#cocoon-asciiart-block-20030311.jar
bzcopy asciiart
#cocoon-batik-block-20030311.jar
bzcopy batik
#cocoon-chaperon-block-20030311.jar
bzcopy chaperon
#cocoon-fop-block-20030311.jar
bzcopy fop
#cocoon-html-block-20030311.jar
bzcopy html
#cocoon-jfor-block-20030311.jar
bzcopy jfor
#cocoon-linkrewriter-block-20030311.jar
bzcopy linkrewriter
#cocoon-lucene-block-20030311.jar
bzcopy lucene
#cocoon-profiler-block-20030311.jar
bzcopy profiler
#commons-collections-2.1.jar
copy commons-collections
#commons-jxpath-1.1b1.jar
copy commons-jxpath
#commons-lang-1.0.1.jar
#excalibur-cli-1.0.jar
copy commons-cli
#excalibur-collections-20020820.jar
copy excalibur-collections
#excalibur-component-20020916.jar
copy excalibur-component
#excalibur-concurrent-20020820.jar
copy excalibur-concurrent
#excalibur-datasource-vm12-20021121.jar
#excalibur-i18n-1.0.jar
copy excalibur-i18n
#excalibur-instrument-20021108.jar
#excalibur-instrument-manager-20021108.jar
#excalibur-instrument-manager-interfaces-20021108.jar
copy excalibur-instrument
#excalibur-io-1.1.jar
copy excalibur-io
#excalibur-logger-20020820.jar
copy excalibur-logger
#excalibur-monitor-20020820.jar
copy excalibur-monitor
#excalibur-naming-1.0.jar
copy excalibur-naming
#excalibur-pool-20020820.jar
copy excalibur-pool
#excalibur-sourceresolve-20030130.jar
copy excalibur-sourceresolve
#excalibur-store-20020820.jar
copy excalibur-store
#excalibur-xmlutil-20030306.jar
copy excalibur-xmlutil
#fop-0.20.4.jar
bcopy fop
#jakarta-oro-2.0.6.jar
#jakarta-regexp-1.2.jar
copy jakarta-regexp
#jing-20020724.jar
copy jing
#jisp-2.0.1.jar
copy jisp
#jtidy-04aug2000r7-dev.jar
bcopy jtidy
#logkit-1.1.jar
copy logkit
#lucene-1.2.jar
bcopy lucene
#nekodtd-20020615.jar
#nekopull.jar
#resolver-20021114.jar
copy resolver
#xml-forrest-components.jar
#xml-forrest-scratchpad.jar


#######
# New jars not in the 2003-03-11 snapshot
copy excalibur-event
copy util.concurrent
