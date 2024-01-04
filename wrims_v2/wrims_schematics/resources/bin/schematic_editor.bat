@echo off
rem ###################################
rem Batch file for running wrims schematic
rem ###################################
setlocal
set wrims_home=%~dp0/..
set Java_Bin_Batch=%~dp0/../jre6/bin/
set WRIMSv2_Engine_Home=%~dp0/../
rem echo %wrims_home%
if exist "%wrims_home%/lib/wrimsv2_SG.jar" goto :valid


:notfound

echo ############################################################
echo   Error: WRIMS Schematic files not found
echo   ___
echo   Installation instructions
echo   ___
echo   The value of the environment variable wrims_home in the 
echo   file wrims_schematic.bat needs to match the location where
echo   WRIMS Schematic has been installed
echo ############################################################
PAUSE
goto :end

:valid
rem ###############
rem Set path to location of dll
rem ###############
set JNILIB=%wrims_home%/lib
set JARDIR=%wrims_home%/lib
set SYSJARDIR=%wrims_home%/lib/sys
set path=%wrims_home%/lib;%path%
:-------------:
: system jars :
:-------------:       
set SYSJARS=%SYSJARDIR%\codebase.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jython.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jythonlib.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jythonUtils.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jxl.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jai_codec.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jai_imageio.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jai_core.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jcommon.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jfreechart.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jh.jar;%SYSJARS%
set SYSJARS=%SYSJARDIR%\jdom.jar;%SYSJARS%
:------------------:
: application jars :
:------------------:
set APPJARS=%JARDIR%\heclib.jar
set APPJARS=%JARDIR%\images.jar;%APPJARS%
set APPJARS=%JARDIR%\rma.jar;%APPJARS%
set APPJARS=%JARDIR%\hecdata.jar;%APPJARS%
set APPJARS=%JARDIR%\gridUtil.jar;%APPJARS%
set APPJARS=%JARDIR%\hec.jar;%APPJARS%
set APPJARS=%JARDIR%\help\dssvueHelp.jar;%APPJARS%
set APPJARS=%JARDIR%\xml.jar;%APPJARS%
set APPJARS=%JARDIR%\wrimsv2.jar;%APPJARS%
set APPJARS=%JARDIR%\vista.jar;%APPJARS%
:-----------------:
: schematic jars  :
:-----------------:
set SCHJARS=%JARDIR%\JDiagram.jar;%JARDIR%\wrimsv2_SG.jar
:------------:
: class path :
:------------:
set CLASSPATH=-classpath %SCHJARS%;%APPJARS%;%SYSJARS%

rem ###############
rem starting wrims schematic
rem ###############
:start
"%wrims_home%/jre6/bin/java" -mx512m  -Djava.library.path="%JNILIB%" %CLASSPATH%  wrims.schematic.jdiagram.SchematicEditorFrame "%wrims_home%"
:end
endlocal 
rem 
