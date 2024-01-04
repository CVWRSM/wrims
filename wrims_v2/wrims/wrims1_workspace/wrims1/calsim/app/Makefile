# Makefile to update the catalogs for dss files
# get all files under current directory
JAVA_FILES = AppUtils.java DerivedTimeSeries.java Project.java MultipleTimeSeries.java DTSTableModel.java MTSTableModel.java MonthlyReport.java TSMath.java PathPartMapping.java ReportUtils.java AppProps.java CSVParser.java InputTableData.java DefaultTableData.java Study.java Wresler.java WsiDiGenerator.java LookupTableData.java WsiDiTableModel.java

PACKAGE_LOC=calsim/app
# local first then common
include ../local.makefile
include ../common.makefile

main:
	$(JAVA) -classpath $(CLASSPATH_DEF) calsim.gui.CalsimGui
