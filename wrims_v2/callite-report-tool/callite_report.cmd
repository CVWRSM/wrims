@echo off
rem ###################################
rem Batch file for running callite report
rem ###################################
setlocal
set callite_home=%~dp0
rem echo %callite_home%
if exist "%callite_home%/lib/callite_report.jar" goto :valid

:notfound

echo ############################################################
echo   Error: CALLITE files not found
echo ############################################################
PAUSE
goto :end

:valid
rem ###############
rem Set path to location of dll
rem ###############
set path=%callite_home%/lib;%path%

rem ###############
rem starting callite
rem ###############
:start
"%callite_home%/jre6/bin/javaw" -mx512m  -Djava.library.path="%callite_home%/lib" -classpath "%callite_home%/lib/callite_report.jar;%callite_home%/lib/vista.jar;%callite_home%/lib/crimson.jar;%callite_home%/jython/dom4j-1.6.1.jar;%callite_home%/lib/dsm2-input-model.jar;%callite_home%/lib/heclib.jar;%callite_home%/lib/itext-hyph-xml.jar;%callite_home%/lib/iText.jar;%callite_home%/lib/jfreechart.jar;%callite_home%/lib/jpy.jar;%callite_home%/lib/pdf-renderer.jar"  gov.ca.dwr.callite.Main %1%
:end
endlocal 
rem 
