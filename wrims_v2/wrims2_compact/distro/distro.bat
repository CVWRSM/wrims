set YYYYMMDD=%DATE:~10,4%%DATE:~4,2%%DATE:~7,2%

set s=..\..\wrims2_compact
set t=z:\wrims2_compact_%YYYYMMDD%

robocopy %s% %t% runConfig.bat runConfig_pause.bat test_different_study.bat test_same_study.bat

robocopy %s%\example %t%\example /E /XD =ILP= /xf *.par *.log *.dsc *.dsd *.dsk *_dv.dss .gitignore

robocopy %s%\lib_64 %t%\lib_64 /E /XD other

robocopy %s%\jars %t%\jars /E /xf *_old*

robocopy %s%\jre8_x64 %t%\jre8_x64 /E

rem robocopy ..\..\wrims2_compact %t% /e /XD distro =ILP= others examples jre8* /xf "examples\*" 