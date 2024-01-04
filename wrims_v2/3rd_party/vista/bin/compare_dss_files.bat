@echo off
set VISTA_HOME=%~dp0
set SCRIPT_HOME=%~dp0/../scripts/compare_dss
%vista_home%/vscript.bat %script_home%/compare_dss_files.py %*