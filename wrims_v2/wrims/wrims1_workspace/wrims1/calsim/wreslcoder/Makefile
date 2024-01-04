# Makefile to update the catalogs for dss files
# get all files under current directory

JAVA_FILES = WreslMaker.java WreslIncScanner.java
#
PACKAGE_LOC=calsim/wreslcoder
# local first then common
include ../local.makefile
include ../common.makefile

main: 
	$(JAVA) -classpath $(CLASSPATH_DEF) calsim.gui.CalsimGui
