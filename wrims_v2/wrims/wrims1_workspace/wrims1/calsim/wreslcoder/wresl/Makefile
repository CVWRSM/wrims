# Makefile to update the catalogs for dss files
# get all files under current directory

JAVA_FILES =  WreslParser.java DvarBounder.java LPExpression.java LPTerm.java Penalizer.java ReportWriter.java ReservedWords.java Timeseries.java UniqueList.java
#
PACKAGE_LOC=calsim/wreslcoder/wresl
# local first then common
include ../../local.makefile
include ../../common.makefile

WreslParser.java: wresl.jj
	$(JAVACC) $(JAVACC_OPTIONS) wresl.jj
