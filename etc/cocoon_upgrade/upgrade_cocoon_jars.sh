#!/bin/sh

# Copyright 2002-2004 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

######################################################################
# A script for upgrading Forrest's jars with those from Cocoon.  Use at own
# risk!  Make sure you build Cocoon before running this.  If your cocoon-2.1
# directory is not on the same level as xml-forrest/, set the COCOON_HOME variable
# below.
#
BASE=$PWD/`dirname $0`
## MUST BE OVERRIDDEN:
COCOON_VERSION=2.1.4
COCOON_HOME=$BASE/../../../cocoon-2.1
FORREST=$BASE/../..
NEKODTD_VERSION=0.1.9
NEKODTD_HOME=$BASE/../../../nekodtd-$NEKODTD_VERSION
NEKOPULL_VERSION=0.2.3
NEKOPULL_HOME=$BASE/../../../nekopull-$NEKOPULL_VERSION

## CAN be overridden:
#JARSUFFIX=`date +%Y%m%d`
JARSUFFIX=$COCOON_VERSION

## We need to identify the old Cocoon jar amongst all the others.  This pattern
## identifies it.  By default, we assume a date (see $JARSUFFIX) was used
COCOONJAR_SUFFIX="[0-9]*"

## Decide what kind of update to perform.
### - 'testing' copies them to the distribution in
##   build/dist/shbat/*
## - 'real' copies upgraded jars to lib/core/
## - 'real_with_cvs' copies to lib/core/, and marks jars for removal/addition
##   in CVS.  Should only be performed by Forrest committers
#
UPGRADE_TYPE=testing
#UPGRADE_TYPE=real
#UPGRADE_TYPE=real_with_cvs
#
######################################################################

CLIB=$COCOON_HOME/lib/*
CBUILD=$COCOON_HOME/build/cocoon-$COCOON_VERSION
CBLOCKS=$CBUILD/blocks

FDIST=$FORREST/build/dist/shbat

## Determines where to copy new jars to:

if [ "$UPGRADE_TYPE" = "real" -o "$UPGRADE_TYPE" = "real_with_cvs" ]; then
  FLIB=$FORREST/lib/core
  FLIB_ENDORSED=$FORREST/lib/endorsed
elif [ "$UPGRADE_TYPE" = "testing" ]; then
  FLIB=$FDIST/lib/core
  FLIB_ENDORSED=$FDIST/lib/endorsed
fi

alias push="pushd . > /dev/null"
alias pop="popd > /dev/null"

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
  checkdir "$COCOON_HOME" COCOON_HOME
  checkdir "$FLIB" FLIB
  checkdir "$NEKODTD_HOME" NEKODTD_HOME
  checkdir "$NEKOPULL_HOME" NEKOPULL_HOME
#
}

function copy()
{
  echo -n "Copying jar:		$1		"
  push
  cd $FLIB
  rm $1*
  cp $CLIB/$1* .
  pop
  echo "done"
}

# Copy a block's compiled jar
function bzcopy()
{
  echo -n "Copying block jar:	$1			"
  push
#  echo "Updating $FLIB/cocoon-$1-block-* = `ls $FLIB/cocoon-$1-block-*`"
  cd $FLIB
  rm cocoon-$1-block-*.jar
  cp $CBLOCKS/$1-block.jar cocoon-$1-block-$JARSUFFIX.jar
  pop
  echo "done"
}

# Copy across a block's jar dependencies
function bcopy()
{
  echo -n "Copying block dep:	$1			"
  rm $FLIB/$1*
  cp $COCOON_HOME/src/blocks/*/lib/$1* $FLIB/
  echo "done"
}

function upgrade_neko()
{
  echo -n "Updating nekopull and nekodtd		"
  push
  cd $FLIB
  rm neko{dtd,pull}*
  cp $NEKODTD_HOME/nekodtd.jar nekodtd-$NEKODTD_VERSION.jar
  cp $NEKOPULL_HOME/nekopull.jar nekopull-$NEKOPULL_VERSION.jar
  pop
  echo "done"

}

function upgrade_endorsed()
{
  echo -n "Updating endorsed jars		"
  push
  cd $FLIB_ENDORSED
  rm xalan* xerces* xml-apis*

  cp $CLIB/{xalan,xerces,xml-apis}* .

  pop
  echo "done"
}

function copy_local_blocks_properties()
{
  echo -n "Copy local.blocks.properties to $COCOON_HOME 		"
  push
  cp -bu local.blocks.properties $COCOON_HOME
  pop
  echo "done"
}

function build_cocoon()
{
  echo -n "Builing Cocoon 		"
  push
  cd $COCOON_HOME
  $COCOON_HOME/build.sh clean
  $COCOON_HOME/build.sh
  pop
  echo "done"
}


echo "Performing $UPGRADE_TYPE upgrade. New jars copied to:"
echo "  $FLIB"
echo "  $FLIB_ENDORSED"


sanity_check
#Commented by default
#copy_local_blocks_properties
#build_cocoon

upgrade_neko
upgrade_endorsed

#set -vx
#avalon-framework-4.1.3.jar
copy avalon-framework
#batik-all-1.5b2.jar
#bcopy batik-all
#chaperon-20030208.jar
bcopy chaperon
#cocoon-20030311.jar
rm $FLIB/cocoon-$COCOONJAR_SUFFIX.jar ; cp $CBUILD/cocoon.jar $FLIB/cocoon-$JARSUFFIX.jar
rm $FLIB/cocoon-deprecated-*.jar ; cp $CBUILD/cocoon-deprecated.jar $FLIB/cocoon-deprecated-$JARSUFFIX.jar
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
#bzcopy jfor
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
#excalibur-component-20020916.jar
copy excalibur-component
#excalibur-concurrent-20020820.jar
#copy excalibur-concurrent
copy util.concurrent
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
#bcopy fop
#jakarta-oro-2.0.6.jar
#jakarta-regexp-1.2.jar
copy jakarta-regexp
# We'll keep our own jing thankyou
#copy jing
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
copy xml-commons-resolver
#xml-forrest-components.jar
#xml-forrest-scratchpad.jar


#######
# New jars not in the 2003-03-11 snapshot
copy excalibur-event
copy util.concurrent

if [ "$UPGRADE_TYPE" = "real_with_cvs" ]; then
  UPDATEFILE=/tmp/forrest-updates
  push
  cd $FORREST
  echo "Diffing against CVS.."
  cvs -n up > $UPDATEFILE
  # Add new jars
  NEWFILES=`cat $UPDATEFILE | grep ^\? | cut -d\  -f 2`
  OLDFILES=`cat $UPDATEFILE | grep ^U | cut -d\  -f 2`
  if [ ! -z "$NEWFILES" ]; then
      echo "Marking new files for addition to CVS:  $NEWFILES"
      cvs add -kb $NEWFILES
  fi
  # Remove old jars
  if [ ! -z "$OLDFILES" ]; then
      echo "Marking removed files for deletion from CVS: $OLDFILES"
      cvs remove -f $OLDFILES
  fi
  pop
fi

echo "All done.  Upgraded Cocoon jars copied to:"
echo "  $FLIB"
echo "  $FLIB_ENDORSED"

# vim: set noexpandtab list:
