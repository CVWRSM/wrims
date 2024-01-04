@echo off
set control_file=generateInerfaceAnn.txt

set wrims_home=%~dp0/..
set JNILIB=%wrims_home%/lib

"%wrims_home%/jre6/bin/java" -Djava.library.path="%JNILIB%" -cp "%JNILIB%/WRIMSv2.jar" wrimsv2.external.GenerateCompileFiles "%control_file%" %~dp0
start /w processDll

copy wrimsv2\external\*.class ..\lib\external\wrimsv2\external
rd /s wrimsv2

del *.o
del *.h
del *.c
del error.log
del processDll.bat
