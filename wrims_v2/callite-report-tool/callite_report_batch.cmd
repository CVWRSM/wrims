@echo off

echo off

IF [%1]==[] (
	echo ==================================
	echo # Error: INP file path missing
	echo ==================================
	pause
	exit
	)

rem ###################################
rem Batch file for running callite report
rem ###################################
setlocal
set callite_home=%~dp0
rem echo %callite_home%

rem ###############
rem Set path to location of dll
rem ###############
set path=%callite_home%/lib;%path%

rem ###############
rem starting report
rem ###############

%callite_home%/jre6/bin/java -mx512m  -Djava.library.path="%callite_home%/lib" -classpath "%callite_home%/lib/callite_report.jar;%callite_home%/lib/vista.jar;%callite_home%/lib/crimson.jar;%callite_home%/jython/dom4j-1.6.1.jar;%callite_home%/lib/dsm2-input-model.jar;%callite_home%/lib/heclib.jar;%callite_home%/lib/itext-hyph-xml.jar;%callite_home%/lib/iText.jar;%callite_home%/lib/jfreechart.jar;%callite_home%/lib/jpy.jar;%callite_home%/lib/pdf-renderer.jar"  gov.ca.dwr.callite.Batch %1%

exit



