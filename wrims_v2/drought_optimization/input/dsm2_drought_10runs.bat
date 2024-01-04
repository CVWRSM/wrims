
@echo off
SETLOCAL

::FOR /f "eol=;tokens=1,3 delims=," %%x IN (runs.txt) DO call :item %%x  
FOR /f "eol=;tokens=1,3 delims=," %%x IN (runs.txt) DO call :item %%x %%y %%z %%w

goto end

:item
echo Drought Barrier  :  %1
echo DCU scenario     :  %2
echo DXC Gates OP     :  %3
echo DXC Gates OP     :  %4

set RunNo=%1
set DCUNo=%2
set DXCNo=%3
set ForNo=%4


hydro historical_hydro_droughtstudy.inp
:: rename .\output\historical_v81.dss historical_v81_hydro.dss
qual historical_qual_ec_droughtstudy.inp
:: rename .\output\historical_v81.dss historical_v81_qual.dss
::qual historical_qual_vol_droughtstudy.inp

::.\..\..\..\bin\hydro_new.exe .\dsm2.inp

goto end

:end

ENDLOCAL

:DOSEXIT


