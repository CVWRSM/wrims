@ echo off

set build_config=%1
if "%build_config%"=="" set build_config=release_static

set msversion=vs9
echo msversion=%msversion%
echo build configuration=%build_config%


set heclib_name=heclib
set heclib_cname=%heclib_name%_c
set heclib_fname=%heclib_name%_f

set config_suffix=notset
if "%build_config%"=="debug_static" set config_suffix=_debug
if "%build_config%"=="debug_dll" set config_suffix=_dll_debug
if "%build_config%"=="release_static" set config_suffix=
if "%build_config%"=="release_dll" set config_suffix=_dll

if "%config_suffix%"=="notset" (
echo configuration not recognized!!!!
goto end
)

set heclib=%heclib_name%_%msversion%%config_suffix%.lib
set heclib_c=%heclib_cname%_%msversion%%config_suffix%.lib
set heclib_f=%heclib_fname%_%msversion%%config_suffix%.lib

set output=.\bin\%build_config%

if not exist %output% mkdir %output%
del /Q %output%\*.*

@echo on
copy .\%build_config%\%heclib% %output%\.
copy ..\heclib_c\%build_config%\%heclib_c% %output%\.
copy ..\heclib_f\%build_config%\%heclib_f% %output%\.
cd %output%
@echo off
(LIB %heclib% %heclib_c% %heclib_f% && echo Combination of libraries successful. Redefinition warnings should be harmless && del %heclib_c% && del %heclib_f%) || del *.lib
cd ..\..

:end
@echo on