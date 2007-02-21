The IVY repository project contains two sets of resources:

1) a set of jars download by IVY
2) a set of ANT scripts for IVY projects

The jars downloadable by IVY are nothing more than a 
standard IVY repository, so to make use of this you 
ought to read the IVY documentation. The ANT scripts,
however, require some introduction.

ANT Scripts
-----------

There are a number of ANT scripts in the build-scripts directory
these are intended to be imported by your project and provide
a "standard" set of targets for working with projects of various
types. Each of which will utilise this IVY repository to manage
their dependencies.

The scripts provided are hierarchical, that is each imports 
another script "lower" in the hierarchy. This means that your
project should only import one of these scripts. Each of the 
scripts handles a single set of requirements for a specific
type of project. Working from the lowest level of the hierarchy
we have:

common-ivy.xml
--------------

This build file provides the targets and configuration necessary
for working with IVY repositories such as this one

common-java.xml
---------------

Here we have targets needed to manage a typical Java project.

This file imports the common-ivy.xml file.


Your project may need to do some special processing for one
or more of the targets in this build file. The following
targets are intended to be overridden by your project build
file for this purpose.

- post-compile-core
  This target is called after the compiation of core files. 
  It should be used to handle  any non-standard compilation
  tasks. For example, it may copy some resources into the 
  classpath.

common-webapp.xml
---------------

Here we have targets needed to manage a typical java web 
application project.

This file imports the common-java.xml file.

Your project may need to do some special processing for one
or more of the targets in this build file. The following
targets are intended to be overridden by your project build
file for this purpose.

- post-deploy-webapp
  This target is called after to the depoyment of webapp files
  to the webap server. It should be used to handle 
  any non-standard parts of the webapp. For example, it may 
  copy some resources into the webapp folder.

Project build.xml
-----------------

Your project build file should import one of the above common 
build scripts. For example:

<?xml version="1.0" encoding="UTF-8"?>
<project name="anhydrite" default="deploy-webapp">
	<property file="build.properties"/>
	<import file="${ivy.repository.dir}/build-scripts/common-webapp.xml"/>
</project>

Project ivy.xml
---------------

Your project must provide an ivy.xml file. For example:

<?xml version="1.0" encoding="ISO-8859-1"?>
<ivy-module version="1.0">
    <info organisation="SAAfE" module="org.saafe.model" revision="20070102000038" status="integration" publication="20070102000038">
	</info>
	<configurations>
	  <conf name="default"/>
	  <conf name="test" extends="default"/>
	</configurations>
	<publications>
	  <artifact name="org.saafe.model" type="jar" ext="jar"/>
	</publications>
</ivy-module>

