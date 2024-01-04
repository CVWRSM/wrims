@echo off
::  usage: csdp_geom_convert directory_containing_csdp_calculated_cross_sections
echo 
echo -------------------------------------
echo Reads a directory of CSDP generated cross sections and writes them in the new 
echo geometry format for DSM2 in the same directory with name "irregular_xsections_dsm2.inp"
echo -------------------------------------
echo
set VISTA_HOME=%~dp0
set SCRIPT_HOME=%~dp0/../scripts/dsm2
%vista_home%/vscript.bat %script_home%/csdp_geom_converter.py %1
