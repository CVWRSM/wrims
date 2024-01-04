@echo off
set MBACKUP=%CLASSPATH%
set CLASSPATH=C:\javacc2.0\bin\lib\JavaCC.zip
java COM.sun.labs.jjdoc.JJDocMain %1 %2 %3 %4 %5 %6 %7 %8 %9
set CLASSPATH=%MBACKUP%
