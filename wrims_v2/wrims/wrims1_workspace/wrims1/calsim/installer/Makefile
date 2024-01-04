# Makefile to update the catalogs for dss files
# get all files under current directory
PACKAGE=
#
JAVA_FILES = CalsimBuilder.java BatchFileConfigTask.java
#
PACKAGE_LOC=./
#
include ../local.makefile
include ../common.makefile

CLASSPATH_DEF = ".${CPATH_SEP}${WEBSTART_LIB}${CPATH_SEP}${JAVA_HOME}/lib/classes.zip"

installer: compile
	${JAVA} -classpath ${CLASSPATH_DEF} CalsimBuilder
	zip -r ../calsim.zip calsim.class install.bat
	rm -rf *.class

compile: ${JAVA_FILES}
	${JAVAC} -classpath ${CLASSPATH_DEF} ${JAVA_FILES}
