# Makefile to update the catalogs for dss files
# get all files under current directory
JAVA_FILES = DebugSetting.java 

PACKAGE_LOC=calsim/debug
# local first then common
include ../local.makefile
include ../common.makefile

main:
	$(JAVA) -classpath $(CLASSPATH_DEF) calsim.gui.CalsimGui
